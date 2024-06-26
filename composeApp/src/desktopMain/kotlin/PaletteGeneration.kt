import com.sun.jna.platform.win32.Advapi32Util
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Stream


class PaletteGeneration {
    fun readRegistry(local: String, key: String): String {
        val builder = ProcessBuilder(
            "reg", "query",
            local
        )
        val reg = builder.start()
        return BufferedReader(
            InputStreamReader(reg.inputStream)
        ).use { output ->
            val keys: Stream<String> =
                output.lines().filter { l: String -> l.isNotEmpty() }
            val matches: Stream<String> = keys.filter { l -> l.contains("\\$key") }
            matches.findFirst().orElse("nulo")
        }

    }

    fun getAccentColor(): Int {
        return try {
            Advapi32Util.registryGetIntValue(
                com.sun.jna.platform.win32.WinReg.HKEY_CURRENT_USER,
                "Software\\Microsoft\\Windows\\DWM",
                "ColorizationColor"
            )
        } catch (e: Exception) {
            Advapi32Util.registryGetIntValue(
                com.sun.jna.platform.win32.WinReg.HKEY_CURRENT_USER,
                "Software\\Microsoft\\Windows\\DWM",
                "AccentColor"
            )
        }
    }

}

/**
 * Modifies the Windows Registry for the Accent Color
 */
enum class WinRegistry {
    WIN_REGISTRY;

    companion object {
        private const val DWM_PATH = ""
        private const val KEY = ""

    }
}