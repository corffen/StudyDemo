package com.gordon.studydemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gordon.studydemo.databinding.ActivityMainBinding
import com.sensorsdata.anlytics.SensorsDataAPI
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tv1.setOnClickListener {
            try {
                val properties = JSONObject()
                properties.put("ProductID", 123456) // 设置商品 ID
                properties.put("ProductCatalog", "Laptop Computer") // 设置商品类别
                SensorsDataAPI.sharedInstance().track("BuyProduct", properties)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        binding.tv2.setOnClickListener {
            repeat(10) { i ->
                try {
                    val properties = JSONObject()
                    properties.put("ProductID", 123456 + i) // 设置商品 ID
                    properties.put("ProductCatalog", "Laptop Computer$i") // 设置商品类别
                    SensorsDataAPI.sharedInstance().track("BuyProduct", properties)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
}