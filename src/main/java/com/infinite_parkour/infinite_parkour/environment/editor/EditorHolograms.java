package com.infinite_parkour.infinite_parkour.environment.editor;

import com.infinite_parkour.infinite_parkour.data.JumpData;
import com.infinite_parkour.infinite_parkour.data.JumpPack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EditorHolograms {
	private final List<Optional<JumpData>> jumps = new ArrayList<>();

	public void saveInto(JumpPack pack) {
		List<JumpData> result = pack.jumps();
		result.clear();
		for (Optional<JumpData> jump : jumps) {
			jump.ifPresent(result::add);
		}
	}

	public void loadFrom(JumpPack pack) {
		jumps.clear();
		for (JumpData jump : pack.jumps()) {
			jumps.add(Optional.of(jump));
		}
		while (jumps.size() % 30 != 0) {
			jumps.add(Optional.empty());
		}
	}
}
