import com.pgt.internal.util.RepositoryUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Yove on 10/24/2015.
 */
public class Ognl {

	public static boolean isNotBlankString(String pString) {
		return StringUtils.isNotBlank(pString);
	}

	public static boolean isValidId(String pId) {
		return RepositoryUtils.idIsValid(pId);
	}

	public static boolean isValidId(int pId) {
		return RepositoryUtils.idIsValid(pId);
	}

	public static boolean isValidId(Integer pId) {
		return RepositoryUtils.idIsValid(pId);
	}

}