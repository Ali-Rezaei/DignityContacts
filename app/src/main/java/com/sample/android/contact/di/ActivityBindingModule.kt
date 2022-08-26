package com.sample.android.contact.di

import com.sample.android.contact.ui.contact.MainActivity
import com.sample.android.contact.ui.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = [MainModule::class])
    internal abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun splashActivity(): SplashActivity
}