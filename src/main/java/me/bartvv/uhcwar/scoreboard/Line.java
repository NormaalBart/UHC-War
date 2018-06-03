package me.bartvv.uhcwar.scoreboard;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import me.bartvv.uhcwar.UHCWar;
import me.bartvv.uhcwar.interfaces.IPlaceHolder;
import me.bartvv.uhcwar.manager.User;

public class Line {

	private UHCWar uhcWar;
	private List<String> lines = Lists.newArrayList();
	private int iterations_needed;
	private int scroll_pos = -1;
	private int iterations = 0;
	private User user;
	private int entries = 0;
	private Set<IPlaceHolder> placeholders;

	public Line(User user, String row, Set<IPlaceHolder> placeholders, UHCWar uhcWar, String path) {
		this.uhcWar = uhcWar;
		this.user = user;
		this.placeholders = Sets.newHashSet();
		this.iterations_needed = this.uhcWar.getScoreboard().getInt(path + "." + row + ".update");
		this.iterations = this.iterations_needed;
		for (String s : this.uhcWar.getScoreboard().getStringList(path + "." + row + ".content")) {
			this.lines.add(s);
			this.entries += 1;
			for (IPlaceHolder placeholder : placeholders) {
				if (s.contains(placeholder.getPlaceholder())) {
					this.placeholders.add(placeholder);
				}
			}
		}
	}

	public String next() {
		this.iterations += 1;
		if (this.iterations >= this.iterations_needed) {
			this.scroll_pos += 1;
		}
		if (this.iterations >= this.iterations_needed) {
			this.iterations = 0;
		}
		if (this.scroll_pos >= this.entries) {
			this.scroll_pos = 0;
		}
		String message = this.lines.get(this.scroll_pos);
		for (IPlaceHolder placeholder : placeholders) {
			message = placeholder.replacePlaceHolder(user, message);
		}
		return message;
	}
}
