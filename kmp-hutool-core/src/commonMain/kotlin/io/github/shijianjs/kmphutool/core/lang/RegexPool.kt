package io.github.shijianjs.kmphutool.core.lang

/**
 * Common regular expressions.
 *
 * Port of `cn.hutool.core.lang.RegexPool`. Values are generated from the upstream source so the
 * patterns stay byte-for-byte identical except where Java syntax is not valid under Kotlin/JS's Unicode regex mode.
 *
 * @hutool cn.hutool.core.lang.RegexPool
 */
object RegexPool {
    val GENERAL: String = "^\\w+$"
    val NUMBERS: String = "\\d+"
    val WORD: String = "[a-zA-Z]+"
    val CHINESE: String = "[\u2E80-\u2EFF\u2F00-\u2FDF\u31C0-\u31EF\u3400-\u4DBF\u4E00-\u9FFF\uF900-\uFAFF\uD840\uDC00-\uD869\uDEDF\uD869\uDF00-\uD86D\uDF3F\uD86D\uDF40-\uD86E\uDC1F\uD86E\uDC20-\uD873\uDEAF\uD87E\uDC00-\uD87E\uDE1F]"
    val CHINESES: String = CHINESE + "+"
    val GROUP_VAR: String = "\\$(\\d+)"
    val IPV4: String = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)$"
    val IPV6: String = "(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))"
    val MONEY: String = "^(\\d+(?:\\.\\d+)?)$"
    val EMAIL: String = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
    val EMAIL_WITH_CHINESE: String = "[A-Za-z0-9.\u4e00-\u9fa5_%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
    val MOBILE: String = "(?:0|86|\\+86)?1[3-9]\\d{9}"
    val MOBILE_HK: String = "(?:0|852|\\+852)?\\d{8}"
    val MOBILE_TW: String = "(?:0|886|\\+886)?(?:|-)09\\d{8}"
    val MOBILE_MO: String = "(?:0|853|\\+853)?(?:|-)6\\d{7}"
    val TEL: String = "(010|02\\d|0[3-9]\\d{2})-?(\\d{6,8})"
    val TEL_400_800: String = "0\\d{2,3}[\\- ]?[0-9]\\d{6,7}|[48]00[\\- ]?[0-9]\\d{2}[\\- ]?\\d{4}"
    val CITIZEN_ID: String = "[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([012]\\d)|3[0-1])\\d{3}(\\d|X|x)"
    val ZIP_CODE: String = "^(0[1-7]|1[0-356]|2[0-7]|3[0-6]|4[0-7]|5[0-7]|6[0-7]|7[0-5]|8[0-9]|9[0-8])\\d{4}|99907[78]$"
    val BIRTHDAY: String = "^(\\d{2,4})([/\\-.年]?)(\\d{1,2})([/\\-.月]?)(\\d{1,2})日?$"
    val URI: String = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?"
    val URL: String = "[a-zA-Z]+://[\\w\\-+&@#/%?=~_|!:,.;]*[\\w\\-+&@#/%=~_|]"
    val URL_HTTP: String = "(https?|ftp|file)://[\\w\\-+&@#/%?=~_|!:,.;]*[\\w\\-+&@#/%=~_|]"
    val GENERAL_WITH_CHINESE: String = "^[\u4E00-\u9FFF\\w]+$"
    val UUID: String = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
    val UUID_SIMPLE: String = "^[0-9a-fA-F]{32}$"
    val MAC_ADDRESS: String = "((?:[a-fA-F0-9]{1,2}[:-]){5}[a-fA-F0-9]{1,2})|((?:[a-fA-F0-9]{1,4}[.]){2}[a-fA-F0-9]{1,4})|[a-fA-F0-9]{12}|0x(\\d{12}).+ETHER"
    val HEX: String = "^[a-fA-F0-9]+$"
    val TIME: String = "\\d{1,2}[:时]\\d{1,2}([:分]\\d{1,2})?秒?"
    val PLATE_NUMBER: String = "^(([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z](([0-9]{5}[ABCDEFGHJK])|([ABCDEFGHJKP]([A-HJ-NP-Z0-9])[0-9]{4})))|" +

					"([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]\\d{3}\\d{1,3}[领])|" +
					"([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳使领]))$"
    val CREDIT_CODE: String = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$"
    val CAR_VIN: String = "^[A-HJ-NPR-Z0-9]{8}[X0-9]([A-HJ-NPR-Z0-9]{3}\\d{5}|[A-HJ-NPR-Z0-9]{5}\\d{3})$"
    val CAR_DRIVING_LICENCE: String = "^[0-9]{12}$"
    val CHINESE_NAME: String = "^[\u3400-\u9FFF·]{2,60}$"
}
