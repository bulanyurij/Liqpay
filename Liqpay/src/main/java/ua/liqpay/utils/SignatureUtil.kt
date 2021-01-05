package ua.liqpay.utils

/**
 * Create Liqopay signature
 *
 * @param data Base64 encoded data
 * @param privateKey Liqpay private key
 *
 */
fun signature(data: String, privateKey: String): String {
    return ((privateKey + data + privateKey).sha1()).base64()
}
