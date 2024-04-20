package com.balius.coincap.ui.coins

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.balius.coincap.R
import com.balius.coincap.databinding.FragmentCoinsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CoinsFragment : Fragment() , CoinsAdapter.CoinActionListener {
    private var _binding: FragmentCoinsBinding? = null
    private val binding get() = _binding

    private val viewModel: CoinsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCoinsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCoins()

        viewModel.coins.observe(viewLifecycleOwner){
            val adapter = CoinsAdapter(requireActivity(),it, this)
            binding?.recycleCoins?.adapter = adapter
            binding?.recycleCoins?.layoutManager =  LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        }

    }

    override fun onCoinSelect(coinName : String) {
       findNavController().navigate(R.id.action_coinsFragment_to_coinDetailFragment)
    }


}