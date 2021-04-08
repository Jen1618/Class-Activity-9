package com.example.kotlinexample2

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import androidx.lifecycle.Observer
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject

class FragmentTwo : Fragment() {

    private val viewModel: UserViewModel by activityViewModels()
    private lateinit var textView: TextView

    private val apiURL = "http://api.zippopotam.us/us/"
    private val client = AsyncHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_two, container, false)
        textView = view.findViewById(R.id.text_view_two)

        viewModel.getInformation().observe(viewLifecycleOwner, Observer { userInfo ->
            userInfo?.let {
                client.get(apiURL + it.zipcode, object : AsyncHttpResponseHandler() {
                    override fun onSuccess(
                        statusCode: Int,
                        headers: Array<out Header>?,
                        responseBody: ByteArray?
                    ) {
                        if (responseBody == null) {
                            textView.text = it.name + " : Error no city exists based off zipcode"
                        } else {
                            try {
                                val json = JSONObject(String(responseBody))
                                val jsonObject: JSONObject =
                                    json.getJSONArray("places").getJSONObject(0)
                                val city = jsonObject.getString("place name")
                                textView.text = it.name + " : " + city
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }

                    }

                    override fun onFailure(
                        statusCode: Int,
                        headers: Array<out Header>?,
                        responseBody: ByteArray?,
                        error: Throwable?
                    ) {
                        textView.text = it.name + " : Error no city exists based off zipcode"
                    }
                })
            }
        })

        return view
    }
}