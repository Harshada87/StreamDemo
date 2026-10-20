package com.example.streamdemo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule (
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
): TestWatcher(){

    //Before each test: use the test dispatcher as "Main"
    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    //After each test: put the real Main back
    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}