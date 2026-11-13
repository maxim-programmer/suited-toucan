package com.example.drivenext.presentation.addcar

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentAddCarDetailsBinding
import com.example.drivenext.domain.model.CarCatalog
import com.example.drivenext.domain.model.Transmission
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted
import com.example.drivenext.presentation.common.showErrorDialog

class AddCarDetailsFragment : Fragment(R.layout.fragment_add_car_details) {

    private val viewModel: AddCarViewModel by navGraphViewModels(R.id.add_car_graph) {
        appContainer.viewModelFactory
    }

    private var _binding: FragmentAddCarDetailsBinding? = null
    private val binding get() = _binding!!

    private var currentModels: List<String> = emptyList()

    private val transmissionItems: List<String>
        get() = listOf(
            getString(R.string.add_car_transmission_automatic),
            getString(R.string.add_car_transmission_manual)
        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddCarDetailsBinding.bind(view)

        val form = viewModel.form.value
        binding.yearEditText.setText(form.year)
        binding.mileageEditText.setText(form.mileage)
        binding.priceEditText.setText(form.pricePerDay)
        binding.descriptionEditText.setText(form.description)

        binding.yearEditText.doAfterTextChanged { viewModel.onYearChanged(it?.toString().orEmpty()) }
        binding.mileageEditText.doAfterTextChanged { viewModel.onMileageChanged(it?.toString().orEmpty()) }
        binding.priceEditText.doAfterTextChanged { viewModel.onPricePerDayChanged(it?.toString().orEmpty()) }
        binding.descriptionEditText.doAfterTextChanged {
            viewModel.onDescriptionChanged(it?.toString().orEmpty())
        }

        setupBrandSpinner(form.brand)
        showModels(form.brand, form.model)
        setupTransmissionSpinner(form.transmission)

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.submitButton.setOnClickListener { viewModel.onSubmitClicked() }

        collectWhenStarted(viewModel.form) { current ->
            binding.submitButton.isEnabled = current.isDetailsFilled && !viewModel.isLoading.value
        }

        collectWhenStarted(viewModel.isLoading) { loading ->
            binding.progressIndicator.visibility = if (loading) View.VISIBLE else View.GONE
            binding.submitButton.isEnabled = !loading && viewModel.form.value.isDetailsFilled
            binding.backButton.isEnabled = !loading
        }

        collectWhenStarted(viewModel.errors) { errors ->
            binding.yearLayout.error = errors.year?.let { getString(it) }
            binding.mileageLayout.error = errors.mileage?.let { getString(it) }
            binding.priceLayout.error = errors.pricePerDay?.let { getString(it) }
        }

        collectWhenStarted(viewModel.events) { event ->
            when (event) {
                AddCarEvent.OpenPhotos -> findNavController().navigate(R.id.action_details_to_photos)
                is AddCarEvent.ShowError -> showErrorDialog(event.message)
                else -> Unit
            }
        }
    }

    private fun setupBrandSpinner(selectedBrand: String) {
        val brands = CarCatalog.brands
        binding.brandSpinner.adapter = HintSpinnerAdapter(
            requireContext(),
            getString(R.string.add_car_brand_hint),
            brands
        )
        binding.brandSpinner.setSelection(HintSpinnerAdapter.positionOf(brands, selectedBrand), false)
        binding.brandSpinner.onItemSelectedListener = selectionListener { position ->
            val brand = HintSpinnerAdapter.itemAt(brands, position).orEmpty()
            viewModel.onBrandChanged(brand)
            showModels(brand, viewModel.form.value.model)
        }
    }

    private fun showModels(brand: String, selectedModel: String) {
        currentModels = CarCatalog.modelsOf(brand)
        val spinner = binding.modelSpinner
        spinner.onItemSelectedListener = null
        spinner.adapter = HintSpinnerAdapter(
            requireContext(),
            getString(R.string.add_car_model_hint),
            currentModels
        )
        spinner.setSelection(HintSpinnerAdapter.positionOf(currentModels, selectedModel), false)
        spinner.isEnabled = currentModels.isNotEmpty()
        spinner.alpha = if (currentModels.isNotEmpty()) 1f else DISABLED_ALPHA
        spinner.onItemSelectedListener = selectionListener { position ->
            viewModel.onModelChanged(HintSpinnerAdapter.itemAt(currentModels, position).orEmpty())
        }
    }

    private fun setupTransmissionSpinner(selected: Transmission?) {
        val items = transmissionItems
        binding.transmissionSpinner.adapter = HintSpinnerAdapter(
            requireContext(),
            getString(R.string.add_car_transmission_hint),
            items
        )
        val selectedLabel = when (selected) {
            Transmission.Automatic -> items[0]
            Transmission.Manual -> items[1]
            null -> null
        }
        binding.transmissionSpinner.setSelection(HintSpinnerAdapter.positionOf(items, selectedLabel), false)
        binding.transmissionSpinner.onItemSelectedListener = selectionListener { position ->
            val transmission = when (position) {
                1 -> Transmission.Automatic
                2 -> Transmission.Manual
                else -> null
            }
            viewModel.onTransmissionChanged(transmission)
        }
    }

    private fun selectionListener(onSelected: (Int) -> Unit) = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            onSelected(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) = Unit
    }

    override fun onDestroyView() {
        binding.brandSpinner.onItemSelectedListener = null
        binding.modelSpinner.onItemSelectedListener = null
        binding.transmissionSpinner.onItemSelectedListener = null
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val DISABLED_ALPHA = 0.5f
    }
}