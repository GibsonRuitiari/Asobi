package com.gibsonruitiari.asobi

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.window.layout.WindowMetricsCalculator
import com.gibsonruitiari.asobi.common.ScreenSize
import com.gibsonruitiari.asobi.databinding.ActivityMainBinding
import com.gibsonruitiari.asobi.presenter.viewmodels.MainActivityViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val mainActivityViewModel:MainActivityViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val container:ViewGroup = binding.root
        // https://issuetracker.google.com/202338815
        // hook a utility view to listen to configurationChanges
        container.addView(object:View(this){
            override fun onConfigurationChanged(newConfig: Configuration?) {
                super.onConfigurationChanged(newConfig)
                mainActivityViewModel.setScreenWidth(computeWindowSizeClasses())
            }
        })

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                mainActivityViewModel.screenWidthState.collect{
                    when(it){
                        ScreenSize.COMPACT->{
                            // add a compact view to the root container
                        }
                        ScreenSize.MEDIUM->{

                        }
                        ScreenSize.EXPANDED->{

                        }
                    }
                }
            }
        }
    }
    private fun computeWindowSizeClasses():ScreenSize{
        val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
        val widthDp = metrics.bounds.width()/resources.displayMetrics.density
        return when{
            widthDp < 600f -> ScreenSize.COMPACT
            widthDp < 640f-> ScreenSize.MEDIUM
            else -> ScreenSize.EXPANDED
        }

    }
    companion object{
//        val discoverViewModelKey = object : CreationExtras.Key<ObserveLatestComics>{}
//        val mutableCreationExtras:MutableCreationExtras = MutableCreationExtras().apply {
//            set(discoverViewModelKey, ObserveLatestComics())
//        }
//        val discoverViewModelFactory = object :ViewModelProvider.Factory{
//            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
//                return when(modelClass){
//                    DiscoverViewModel::class.java->{
//                        mutableCreationExtras[discoverViewModelKey]?.let {
//                            param->DiscoverViewModel(param)
//                        }
//                    }
//                    else-> throw IllegalArgumentException("Unknown view model $modelClass")
//                }as T
//            }
//        }

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}