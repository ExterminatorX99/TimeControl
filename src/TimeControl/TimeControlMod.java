package TimeControl;

import arc.Core;
import arc.Events;
import arc.func.Boolp;
import arc.graphics.Color;
import arc.scene.ui.Button;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.input.MobileInput;
import mindustry.input.PlaceMode;
import mindustry.mod.Mod;
import mindustry.ui.Styles;

@SuppressWarnings("unused")
public class TimeControlMod extends Mod {

	private final int longPress = 60;
	private final Seq<Float> speedArr = new Seq<>(new Float[]{0.25f, 0.5f, 1f, 2f, 4f});
	private float current = 1;
	private boolean folded = false;
	private int mode = 2;

	public TimeControlMod() {
		if (!Vars.headless) {
			Table ut = new Table();
			Table ft = new Table();

			Events.on(ClientLoadEvent.class, e -> {
				ut.bottom().left();
				ft.bottom().left();
				addTable(ut);
				addMiniT(ft);
				Vars.ui.hudGroup.addChild(ut);
				Vars.ui.hudGroup.addChild(ft);
			});
		}
	}

	private Cell<Button> addSpeed(Table t, float speed) {
		Button b = new Button(Styles.logict);
		b.label(() -> (current == speed ? "[#a9d8ff]" : "[white]") + "x" + speed + "[]");
		b.clicked(() -> {
			Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60 * speed, 3 * speed));
			current = speed;
		});
		return t.add(b).size(50, 40).color(Pal.lancerLaser).pad(1);
	}


	private Cell<Button> addSpeedAlt(Table t, float speed, float speed2) {
		Button b = new Button(Styles.logict);
		b.label(() -> (current == speed ? "[#a9d8ff]" : (current == speed2 ? "[accent]" : "[white]")) + "x" + (current == speed2 ? speed2 : speed) + "[]");
		b.clicked(() -> {
			if (current == speed) {
				Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60 * speed2, 3 * speed2));
				current = speed2;
				b.setColor(Pal.accent);
			} else {
				Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60 * speed, 3 * speed));
				current = speed;
				b.setColor(Pal.lancerLaser);
			}
		});
		b.update(() -> b.setColor(current == speed2 ? Pal.accent : Pal.lancerLaser));
		return t.add(b).size(50, 40).color(Pal.lancerLaser).pad(1);
	}

	private Cell<Button> addSpeedThree(Table t, float speed, float speed2, float speed3) {
		Button b = new Button(Styles.logict);
		final float[] h3 = {0};
		b.label(() -> (current == speed ? "[#a9d8ff]" : (current == speed2 ? "[accent]" : (current == speed3 ? "[green]" : "[white]"))) + "x" + (current == speed2 ? speed2 : (current == speed3 ? speed3 : speed)) + "[]");
		b.clicked(() -> {
			if (h3[0] > longPress) return;
			if (current == speed) {
				Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60 * speed2, 3 * speed2));
				current = speed2;
				b.setColor(Pal.accent);
			} else {
				Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60 * speed, 3 * speed));
				current = speed;
				b.setColor(Pal.lancerLaser);
			}
		});
		b.update(() -> {
			if (b.isPressed()) {
				h3[0] += Core.graphics.getDeltaTime() * 60;
				if (h3[0] > longPress) {
					Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60 * speed3, 3 * speed3));
					current = speed3;
					b.setColor(Color.green);
				}
			} else {
				h3[0] = 0;
			}
			b.setColor(current == speed2 ? Pal.accent : (current == speed3 ? Color.green : Pal.lancerLaser));
		});
		return t.add(b).size(50, 40).color(Pal.lancerLaser).pad(1);
	}

	private Cell<Button> addOne(Table t, int speed) {
		Button b = new Button(Styles.logict);
		final float[] h = {0};
		b.label(() -> (current == speed ? "[#a9d8ff]" : "[white]") + "x" + speed + "[]");
		b.clicked(() -> {
			if (h[0] > longPress) return;
			Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60 * speed, 3 * speed));
			current = speed;
		});
		b.update(() -> {
			if (b.isPressed()) {
				h[0] += Core.graphics.getDeltaTime() * 60;
				if (h[0] > longPress) {
					folded = true;
					mode = speedArr.indexOf(current);
					if (mode < 0) mode = speedArr.size;
				}
			} else {
				h[0] = 0;
			}
		});
		return t.add(b).size(50, 40).color(Pal.lancerLaser).pad(1);
	}

	private Cell<Button> addMini(Table t, Seq<Float> speedlist, String[] showlist) {
		Button b = new Button(Styles.logict);
		final float[] h2 = {0};
		b.label(() -> (current == 1 ? "[#a9d8ff]" : (current > 1.9 ? "[accent]" : "[orange]")) + showlist[mode] + "[]");
		b.clicked(() -> {
			if (h2[0] > longPress) return;
			mode++;
			if (mode >= speedlist.size) mode = 0;
			Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60 * speedlist.get(mode), 3 * speedlist.get(mode)));
			current = speedlist.get(mode);
		});
		b.update(() -> {
			if (b.isPressed()) {
				h2[0] += Core.graphics.getDeltaTime() * 60;
				if (h2[0] > longPress) folded = false;
			} else {
				h2[0] = 0;
			}
			b.setColor(current == 1 ? Pal.lancerLaser : (current > 1.9 ? Pal.accent : Color.orange));
		});
		return t.add(b).size(40, 40).color(Pal.lancerLaser).pad(1).padLeft(0).padRight(0);
	}

	private void addTable(Table table) {
		table.table(Styles.black5, t -> {
			t.background(Tex.buttonEdge3);
			if (Vars.mobile) {
				addSpeedThree(t, 0.5f, 0.25f, 0.125f).width(60);
				addOne(t, 1).width(45);
				addSpeedThree(t, 2, 8, 64).width(45);
				addSpeedThree(t, 4, 16, 256).width(45);
			} else {
				addSpeedAlt(t, 0.25f, 0.125f).width(65);
				addSpeed(t, 0.5f);
				addOne(t, 1);
				addSpeedThree(t, 2, 8, 64);
				addSpeedThree(t, 4, 16, 256).width(65);
			}

			//t.visibility = () -> !folded;
		});
		table.fillParent = true;
		Boolp schem = () -> Vars.control.input.lastSchematic != null && !Vars.control.input.selectRequests.isEmpty();
		Boolp mobile = () -> {
			MobileInput input = (MobileInput) Vars.control.input;
			return !(Vars.player.unit().isBuilding()
					|| Vars.control.input.block != null
					|| input.mode == PlaceMode.breaking
					|| !Vars.control.input.selectRequests.isEmpty()
					&& !schem.get());
		};
		table.visibility = () -> !folded
				&& Vars.ui.hudfrag.shown
				&& !Vars.ui.minimapfrag.shown()
				&& (!Vars.mobile || mobile.get());
	}

	private void addMiniT(Table table) {
		table.table(Styles.black5, t -> {
			t.background(Tex.buttonEdge3);
			if (Vars.mobile) addMini(t, speedArr, new String[]{".25", ".5", "x1", "x2", "x4", "×?"});
			else addMini(t, speedArr, new String[]{"x.25", "x0.5", "x1", "x2", "x4", "x8+"}).width(60);
		});
		table.fillParent = true;
		Boolp schem = () -> Vars.control.input.lastSchematic != null && !Vars.control.input.selectRequests.isEmpty();
		Boolp mobile = () -> {
			MobileInput input = (MobileInput) Vars.control.input;
			return !(Vars.player.unit().isBuilding()
					|| Vars.control.input.block != null
					|| input.mode == PlaceMode.breaking
					|| !Vars.control.input.selectRequests.isEmpty()
					&& !schem.get());
		};
		table.visibility = () -> folded
				&& Vars.ui.hudfrag.shown
				&& !Vars.ui.minimapfrag.shown()
				&& (!Vars.mobile || mobile.get());
	}

}
