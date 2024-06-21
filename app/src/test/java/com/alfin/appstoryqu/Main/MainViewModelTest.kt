package com.alfin.appstoryqu

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.alfin.appstoryqu.Main.ListCeritaAdapter
import com.alfin.appstoryqu.Main.MainViewModel
import com.alfin.appstoryqu.Respon.ListStoryItem
import com.alfin.appstoryqu.RepositoryUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repositoryUser: RepositoryUser

    private val sampleStories = DataDummy.generateDummyStoryResponse()
    private val sampleToken = "1234567890abcdef1234567890abcdef"

    @Before
    fun initialize() {
        MockitoAnnotations.initMocks(this)
        Mockito.lenient().`when`(repositoryUser.getSession()).thenReturn(flowOf(ModelUser("datadummy@gmail.com", sampleToken, true)))
    }

    @Test
    fun `saat mendapatkan cerita, harus tidak null dan mengembalikan data`() = runTest {
        val pagingData: PagingData<ListStoryItem> = CeritaPagingCourse.createSnapshot(sampleStories)
        val expectedLiveData = MutableLiveData<PagingData<ListStoryItem>>().apply { value = pagingData }
        Mockito.`when`(repositoryUser.getStories(sampleToken)).thenReturn(expectedLiveData)

        val viewModel = MainViewModel(repositoryUser)
        val actualPagingData: PagingData<ListStoryItem> = viewModel.getStories(sampleToken).getOrAwaitValue()

        val asyncDiffer = AsyncPagingDataDiffer(
            diffCallback = ListCeritaAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        asyncDiffer.submitData(actualPagingData)

        assertNotNull(asyncDiffer.snapshot())
        assertEquals(sampleStories.size, asyncDiffer.snapshot().size)
        assertEquals(sampleStories[0], asyncDiffer.snapshot()[0])
    }

    @Test
    fun `saat mendapatkan cerita kosong, harus mengembalikan data kosong`() = runTest {
        val emptyPagingData: PagingData<ListStoryItem> = PagingData.empty()
        val expectedLiveData = MutableLiveData<PagingData<ListStoryItem>>().apply { value = emptyPagingData }
        Mockito.`when`(repositoryUser.getStories(sampleToken)).thenReturn(expectedLiveData)

        val viewModel = MainViewModel(repositoryUser)
        val actualPagingData: PagingData<ListStoryItem> = viewModel.getStories(sampleToken).getOrAwaitValue()

        val asyncDiffer = AsyncPagingDataDiffer(
            diffCallback = ListCeritaAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        asyncDiffer.submitData(actualPagingData)

        assertEquals(0, asyncDiffer.snapshot().size)
    }
}

class CeritaPagingCourse : PagingSource<Int, ListStoryItem>() {
    companion object {
        fun createSnapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}