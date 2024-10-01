package com.caesar84mx.tvmaze.di

import com.caesar84mx.tvmaze.viewmodels.EpisodeDetailsViewModel
import com.caesar84mx.tvmaze.viewmodels.EpisodeDetailsViewModelImpl
import com.caesar84mx.tvmaze.viewmodels.PinCodeViewModel
import com.caesar84mx.tvmaze.viewmodels.PinCodeViewModelImpl
import com.caesar84mx.tvmaze.viewmodels.ShowDetailsViewModel
import com.caesar84mx.tvmaze.viewmodels.ShowDetailsViewModelImpl
import com.caesar84mx.tvmaze.viewmodels.HomeViewModel
import com.caesar84mx.tvmaze.viewmodels.HomeViewModelImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel<HomeViewModel> { HomeViewModelImpl(get(), get(), get()) }
    viewModel<ShowDetailsViewModel> { ShowDetailsViewModelImpl(get(), get(), get()) }
    viewModel<EpisodeDetailsViewModel> { EpisodeDetailsViewModelImpl(get(), get()) }
    viewModel<PinCodeViewModel> { PinCodeViewModelImpl(get(), get()) }
}
