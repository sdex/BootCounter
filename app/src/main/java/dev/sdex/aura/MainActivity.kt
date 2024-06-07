package dev.sdex.aura

import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.sdex.aura.data.DataStore
import dev.sdex.aura.databinding.ActivityMainBinding
import dev.sdex.aura.notification.NotificationHelper
import dev.sdex.aura.notification.NotificationScheduler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding

    private val notificationScheduler = NotificationScheduler()

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                NotificationHelper.showNotification(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            NotificationHelper.showNotification(this)
        }

        binding.total.setText(DataStore.getTotalAllowedDismissals().toString())
        binding.interval.setText(DataStore.getIntervalBetweenDismissals().toString())
        binding.totalDismissalsCounter.text = getString(
            R.string.total_dismissals,
            DataStore.getTotalDismissals()
        )

        binding.save.setOnClickListener {
            try {
                val total = binding.total.text.toString().toLong()
                DataStore.setTotalAllowedDismissals(total)
            } catch (e: Exception) {
                binding.total.setText(DataStore.getTotalAllowedDismissals().toString())
            }
            try {
                val interval = binding.interval.text.toString().toLong()
                DataStore.setIntervalBetweenDismissals(interval)
            } catch (e: Exception) {
                binding.interval.setText(DataStore.getIntervalBetweenDismissals().toString())
            }
        }

        notificationScheduler.scheduleNotification(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bootEvents.collectLatest { events ->
                    if (events != null) {
                        showStatus(events)
                    }
                }
            }
        }
    }

    private fun showStatus(events: List<BootEvent>) {
        if (events.isEmpty()) {
            binding.status.setText(R.string.no_boots_detected)
        } else {
            binding.status.text =
                events.joinToString("\n") { event -> " • ${event.date} - ${event.count}" }
        }
    }
}