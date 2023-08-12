package com.ssidglobal.qrreader.common

import com.google.gson.JsonObject
import java.math.BigInteger
import java.net.NetworkInterface
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Collections
import java.util.regex.Pattern


object Helper {
    fun AccesMac(): String {
        try {
            val all: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.name.equals("eth0", ignoreCase = true)) continue
                val macBytes = nif.hardwareAddress ?: return ""
                val res1 = StringBuilder()
                for (b in macBytes) {
                    // res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b))
                }
                if (res1.length > 0) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (ex: Exception) {
            //handle exception
        }
        return ""
    }

    fun getHash(credentials: String): ByteArray {
        var digest: MessageDigest? = null
        try {
            digest = MessageDigest.getInstance("SHA-256")
        } catch (e1: NoSuchAlgorithmException) {
            // TODO Auto-generated catch block
            e1.printStackTrace()
        }
        digest!!.reset()
        return digest.digest(credentials.toByteArray())
    }

    fun bin2hex(data: ByteArray): String {
        return String.format("%0" + data.size * 2 + "X", BigInteger(1, data))
    }

    fun setJsonRequestBody(params: List<String?>, values: List<String?>): JsonObject {
//        String shaKey = Helper.bin2hex(Helper.getHash(Helper.AccesMac() + bodyParam + Constants.SALT));
        val requestBody = JsonObject()
        for (i in params.indices) {
            requestBody.addProperty(params[i], values[i])
        }
        return requestBody
    }

    fun isValidNumberInput(target: String?): Boolean {
        return Pattern.compile("^[^,]\\d*\\,?\\d*[^,]$").matcher(target).matches()
    }

    fun createloginKey(pin : String) : String {
        return bin2hex(getHash(Constants.MAC_TEST+pin+ Constants.Salt))
    }
}