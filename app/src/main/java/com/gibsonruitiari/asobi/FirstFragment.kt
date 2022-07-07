package com.gibsonruitiari.asobi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.gibsonruitiari.asobi.common.ScreenSize
import com.gibsonruitiari.asobi.databinding.FragmentFirstBinding
import com.gibsonruitiari.asobi.presenter.viewmodels.MainActivityViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val activityMainViewModel:MainActivityViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val container:ViewGroup = binding.root // constraint layout so add views it
     //   binding.buttonFirst.setOnClickListener { findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment) }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                activityMainViewModel.screenWidthState.collect{
                    when(it){
                        ScreenSize.COMPACT->{
                            /* layout grid uses 4 columns so for the recycler view's grid layout we need 2 columns */
                            /* layout grid uses 16.dp gutter so for the recycler view's grid layout spacing in between columns ought to be 16.dp */

                        }
                        ScreenSize.MEDIUM->{
                            /* layout grid uses 8 columns so for the recycler view's grid layout we need 4 columns */
                            /* layout grid uses 24.dp gutter so for the recycler view's grid layout spacing in between columns ought to be 24.dp */
                        }
                        ScreenSize.EXPANDED->{
                            /* layout grid uses 12 columns so for the recycler view's grid layout we need 6 columns */

                        }
                    }
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}