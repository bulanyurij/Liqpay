package ua.liqpay.utils

import org.json.JSONException
import org.json.JSONObject

/**
 * Merge json objects.
 *
 * @param object1 [JSONObject]
 *
 * @param object2 [JSONObject]
 */
fun merge(object1: JSONObject, object2: JSONObject): JSONObject {
    val iter = object2.keys() as Iterator<String>
    while (iter.hasNext()) {
        val key = iter.next()
        try {
            object1.put(key, object2[key])
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    return object1
}