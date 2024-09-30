package com.sena.fincaudita

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentTransaction
import com.sena.fincaudita.Farm.FarmFragment
import com.sena.fincaudita.Supplie.SupplieFragment
import com.sena.fincaudita.Treatment.TreatmentFragment
import com.sena.fincaudita.review.RevisionFragment

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val ctnRevision = view.findViewById<ConstraintLayout>(R.id.cntRevision)
        val ctnFarm = view.findViewById<ConstraintLayout>(R.id.cntFarm)
        val ctnSupplie = view.findViewById<ConstraintLayout>(R.id.cntSupplie)
        val ctnFertilization = view.findViewById<ConstraintLayout>(R.id.cntFertilization)

        ctnRevision.setOnClickListener {
            changeFragment(RevisionFragment.newInstance())
        }
        ctnFarm.setOnClickListener {
            changeFragment(FarmFragment.newInstance())
        }
        ctnSupplie.setOnClickListener {
            changeFragment(SupplieFragment.newInstance())
        }
        ctnFertilization.setOnClickListener {
            changeFragment(TreatmentFragment.newInstance())
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private fun changeFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }
}