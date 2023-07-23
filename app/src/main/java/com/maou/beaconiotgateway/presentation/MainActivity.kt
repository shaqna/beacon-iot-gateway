package com.maou.beaconiotgateway.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.maou.beaconiotgateway.R
import com.maou.beaconiotgateway.databinding.ActivityMainBinding
import com.maou.beaconiotgateway.domain.model.BleDevice
import com.maou.beaconiotgateway.utils.TimeHelper
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothManager.adapter
    }

    private val bleAdapter by lazy {
        BleAdapter()
    }

    private var isScanning = false

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadKoinModules(MainViewModel.inject())

        checkPermissionGrant()
        setupAdapter()
        setupButtonAction()
        observeBleDevice()
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_download -> {
                val scannedDevices = viewModel.state.value.scannedDevices
                Log.d("ScannedDevices", scannedDevices.toString())
                Log.d("ScannedDevices", scannedDevices.size.toString())
                doDownload("${viewModel.distanceScanning}meters", scannedDevices)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == BLE_PERMISSION_REQUEST_CODE) {
            if (!allPermissionGranted()) {
                Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!bluetoothAdapter.isEnabled) {
            enableBluetoothLauncher.launch(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            )
        }
    }

    private fun doDownload(fileName: String, scannedDevices: List<BleDevice>) {
        try {
            val root = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "FileExcelBeacon"
            )

            if (!root.exists()) root.mkdirs()

            val path = File(root, "/$fileName.xlsx")

            val workBook = XSSFWorkbook()
            val outputStream = FileOutputStream(path)

            val headerStyle = workBook.createCellStyle().apply {
                alignment = HorizontalAlignment.CENTER
                fillForegroundColor = IndexedColors.BLUE_GREY.index
                fillPattern = FillPatternType.SOLID_FOREGROUND
                borderTop = BorderStyle.MEDIUM
                borderBottom = BorderStyle.MEDIUM
                borderRight = BorderStyle.MEDIUM
                borderLeft = BorderStyle.MEDIUM
            }

            val font = workBook.createFont().apply {
                fontHeightInPoints = 12
                color = IndexedColors.WHITE.index
                bold = true
            }
            headerStyle.setFont(font)

            val sheet = workBook.createSheet()
            sheet.createRow(0).apply {
                createCell(0).apply {
                    setCellValue("Address")
                    cellStyle = headerStyle
                }

                createCell(1).apply {
                    setCellValue("RSSI")
                    cellStyle = headerStyle
                }
                createCell(2).apply {
                    setCellValue("Time")
                    cellStyle = headerStyle
                }

            }

            for (i in scannedDevices.indices) {
                sheet.createRow(i + 1).apply {
                    createCell(0).apply {
                        setCellValue(scannedDevices[i].deviceAddress)
                    }
                    sheet.setColumnWidth(0, (scannedDevices[i].deviceAddress.length + 100) * 256)

                    createCell(1).apply {
                        setCellValue(scannedDevices[i].rssi.toString() + " dBm")
                    }
                    sheet.setColumnWidth(0, scannedDevices[i].rssi.toString().length * 256)

                    createCell(2).apply {
                        setCellValue(TimeHelper.timestampToDate(scannedDevices[i].timestamp))
                    }
                }

            }

            workBook.write(outputStream)
            outputStream.close()
            Toast.makeText(this@MainActivity, "Data berhasil di ekspor!", Toast.LENGTH_SHORT)
                .show();
        } catch (e: IOException) {
            Log.d("FileError", e.message.toString())
        }

    }

    private fun setupAdapter() {
        binding.rvBleDevice.apply {
            adapter = bleAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun observeBleDevice() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        bleAdapter.setItemList(state.scannedDevices)
                        if (state.scannedDevices.isNotEmpty()) {
                            Log.d("Sending", "${state.scannedDevices.last()}")
                            viewModel.sendBeaconData(state.scannedDevices.last())
                        }
                    }
                }
                launch {
                    viewModel.beaconState.collect { state ->
                        when (state) {
                            is BeaconUiState.OnErrorAction -> {
                                Log.d(TAG, "observeSendingDevice Error: ${state.message}")
                            }

                            is BeaconUiState.OnSuccessAction -> {
                                Log.d(TAG, "observeSendingDevice Success: ${state.message}")
                            }

                            else -> {

                            }

                        }
                    }
                }
            }
        }
    }



    private fun setupButtonAction() {
        binding.apply {
            buttonStart.setOnClickListener {
                startScanning()
            }
            buttonStop.setOnClickListener {
                stopScanning()
            }

            buttonStartPeriodic.setOnClickListener {
                startScanningPeriodic()
            }
        }

    }


    private fun checkPermissionGrant() {
        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(this, blePermissions, BLE_PERMISSION_REQUEST_CODE)
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun startScanningPeriodic() {
        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {

            Toast.makeText(this@MainActivity, "Scan started", Toast.LENGTH_SHORT).show()

            startButtonAvailability(false)

            bleAdapter.apply {
                setItemList(emptyList())
                clear()
            }
            viewModel.startLeScanning()

            Handler().postDelayed({
                stopScanning()
            }, 10000)

        } else {
            requestPermissionLauncher.launch(
                blePermissions
            )
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun startScanning() {
        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {

            Toast.makeText(this@MainActivity, "Scan started", Toast.LENGTH_SHORT).show()
            startButtonPeriodicAvailability(false)

            bleAdapter.apply {
                setItemList(emptyList())
                clear()
            }
            viewModel.startLeScanning()

        } else {
            requestPermissionLauncher.launch(
                blePermissions
            )
        }
    }

    private fun stopScanning() {
        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this@MainActivity, "Scan stopped", Toast.LENGTH_SHORT).show()
            startButtonAvailability(true)
            startButtonPeriodicAvailability(true)
            viewModel.stopLeScanning()
        } else {
            requestPermissionLauncher.launch(
                blePermissions
            )
        }
    }

    private fun startButtonAvailability(state: Boolean) {
        binding.buttonStart.isEnabled = state
    }

    private fun startButtonPeriodicAvailability(state: Boolean) {
        binding.buttonStartPeriodic.isEnabled = state
    }

    private fun allPermissionGranted() = blePermissions.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when {
            permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                startScanning()
            }

            else -> {
                Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_SHORT).show()

            }
        }
    }


    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetooth Enabled!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this, "Bluetooth is required for this app to run", Toast.LENGTH_SHORT
            ).show()
            this.finish()
        }
    }

    companion object {
        const val TAG = "BluetoothAdapter"

        private const val BLE_PERMISSION_REQUEST_CODE = 1

        private val blePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
}