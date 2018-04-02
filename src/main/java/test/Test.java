package test;

import com.d1m.tbmessage.server.database.entity.ProjectDO;
import com.d1m.tbmessage.server.database.util.SqlUtil;
import org.apache.commons.lang3.StringEscapeUtils;

public class Test {
	public static void main(String[] args) {
		String words = "123\'123\"123";

		System.out.println(words.replaceAll("'", "\\\\\'").replaceAll("\"", "\\\\\""));
	}
}
