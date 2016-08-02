import eu.bitwalker.useragentutils.UserAgent;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;
import ua_parser.Client;
import ua_parser.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author felix
 */
public class Start {
    private final int numberOfUserAgents;
    private final Collection<String> userAgents;
    private final boolean printParsingResult;

    private Start() {
        numberOfUserAgents = 10_000;
        userAgents = getBunchOfUserAgents(numberOfUserAgents);
        printParsingResult = false;

        measureUaParser();
        measureUADetector();
        measureUserAgentUtils();
    }

    private static Collection<String> getBunchOfUserAgents(int numberOfUserAgents) {
        Collection<String> userAgents = new ArrayList<>(numberOfUserAgents);
        String[] uaExamples = {
                "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko",
                "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36",
                "Opera/9.80 (X11; Linux i686; Ubuntu/14.10) Presto/2.12.388 Version/12.16",
                "Opera/12.80 (Windows NT 5.1; U; en) Presto/2.10.289 Version/12.02",
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; Media Center PC 6.0; InfoPath.3; MS-RTC LM 8; Zune 4.7)",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A",
                "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_7; da-dk) AppleWebKit/533.21.1 (KHTML, like Gecko) Version/5.0.5 Safari/533.21.1",
                "Opera/12.02 (Android 4.1; Linux; Opera Mobi/ADR-1111101157; U; en-US) Presto/2.9.201 Version/12.02",
        };

        for ( int uaI = 0, uaL = uaExamples.length; --numberOfUserAgents >= 0; uaI++) {
            userAgents.add(uaExamples[uaI % uaL]);
        }

        return userAgents;
    }

    private void measureUaParser() {
        final long startTime = System.nanoTime();
        runUaParser(createUaParser(), userAgents);
        final long endTime = System.nanoTime();
        final double elapsedSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.println("ua_parser took " + elapsedSeconds + " sec. for " + numberOfUserAgents + " user-agent-strings");
    }

    private void runUaParser(Parser uaParser, Collection<String> userAgents) {
        for (String userAgent : userAgents) {
            Client c = uaParser.parse(userAgent);
            if (printParsingResult) {
                System.out.println(userAgent);
                System.out.println("\t" + c.os.family);
                System.out.println("\t" + c.userAgent.family);
            }
        }
    }

    private Parser createUaParser() {
        try {
            return new Parser();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void measureUADetector() {
        final long startTime = System.nanoTime();
        runUADetector(getUADetector(), userAgents);
        final long endTime = System.nanoTime();
        final double elapsedSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.println("UADetector took " + elapsedSeconds + " sec. for " + numberOfUserAgents + " user-agent-strings");
    }

    private void runUADetector(UserAgentStringParser parser, Collection<String> userAgents) {
        for (String userAgent : userAgents) {
            ReadableUserAgent rua = parser.parse(userAgent);
            if (printParsingResult) {
                System.out.println(userAgent);
                System.out.println("\t" + rua.getOperatingSystem().getFamily());
                System.out.println("\t" + rua.getFamily());
            }
        }
    }

    private UserAgentStringParser getUADetector() {
        return UADetectorServiceFactory.getResourceModuleParser();
    }

    private void measureUserAgentUtils() {
        final long startTime = System.nanoTime();
        runUserAgentUtils(userAgents);
        final long endTime = System.nanoTime();
        final double elapsedSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.println("UserAgentUtils took " + elapsedSeconds + " sec. for " + numberOfUserAgents + " user-agent-strings");
    }

    private void runUserAgentUtils(Collection<String> userAgents) {
        for (String userAgent : userAgents) {
            UserAgent ua = UserAgent.parseUserAgentString(userAgent);
            if (printParsingResult) {
                System.out.println(userAgent);
                System.out.println("\t" + ua.getOperatingSystem().getGroup());
                System.out.println("\t" + ua.getBrowser().getGroup());
            }
        }
    }

    public static void main(final String[] argv) {
        new Start();
    }
}
