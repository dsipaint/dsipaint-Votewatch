import java.util.List;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class Utils
{
	public static boolean hasAdmin(Member m)
	{
		if(m.isOwner())
			return true;
		
		List<Role> roles = m.getRoles();
		for(Role r : roles)
		{
			if(r.hasPermission(Permission.ADMINISTRATOR))
				return true;
		}
		return false;
	}
}
