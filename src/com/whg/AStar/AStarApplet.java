package com.whg.AStar;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamTokenizer;
import java.net.URL;
import java.util.Vector;

import com.whg.AStar.pathFinder.Fudge;
import com.whg.AStar.pathFinder.HuristicAStar;
import com.whg.AStar.pathFinder.OneTailAStar;
import com.whg.AStar.pathFinder.PathFinder;
import com.whg.AStar.pathFinder.map.GridCell;
import com.whg.AStar.pathFinder.map.Map;

public class AStarApplet extends java.applet.Applet implements ItemListener, ActionListener {
	
	private boolean isDesign = false;
	private CheckboxGroup group;
	private Checkbox blocks, start, finish;
	private Choice level = new Choice();
	private Choice method = new Choice();
	private Choice preset = new Choice();
	private Choice user = new Choice();
	private Button go, clear, save, load, userSave;

	private Map map = new Map();
	private PathFinder finder = new HuristicAStar();// new OneTailAStar();
	
	public static void main(String args[]) {
		AStarApplet app = new AStarApplet();
		app.isDesign = true;
		Frame f = new Frame();
		f.setSize(612, 482);
		f.setResizable(false);
		f.setLayout(new BorderLayout());
		// f.add(new Button("Save"));
		f.add(app, "Center");
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		app.init();

		f.show();
		app.start();
	}
	// {{DECLARE_CONTROLS
	// }}

	@Override
	public void init() {
		// {{INIT_CONTROLS
		setLayout(new BorderLayout(0, 0));
		setSize(612, 482);
		// }}
		Panel m = new Panel();
		method.add("Classic A*");
		method.add("Old");
		method.add("Fudge");
		user.add("Author");
		user.add("Users");
		if (!isDesign) {
			// we are an applet
			// userSave = new Button("Save");
			// m.add(new Label("Map Set:"));
			// m.add(user);
			m.add(new Label("Load Map:"));
			m.add(preset);
			// m.add(userSave);
			findSavedMaps("Saves");

		}
		m.add(new Label("Method:"));
		m.add(method);
		add(m, "North");
		add(map, "Center");
		Panel p = new Panel();
		p.setLayout(new GridLayout(4, 1));
		Panel b = new Panel();
		b.setLayout(new GridLayout(2, 1));
		level.add("Impossible");
		level.add("Very Tough (" + GridCell.VERY_TOUGH + ")");
		level.add("Tough (" + GridCell.TOUGH + ")");
		level.add("Normal (" + GridCell.NORMAL + ")");
		level.add("Easy (" + GridCell.EASY + ")");

		level.addItemListener(this);
		preset.addItemListener(this);
		method.addItemListener(this);

		group = new CheckboxGroup();
		blocks = new Checkbox("set blocks", group, true);
		start = new Checkbox("set start", group, false);
		finish = new Checkbox("set finish", group, false);
		blocks.addItemListener(this);
		start.addItemListener(this);
		finish.addItemListener(this);
		b.add(blocks);
		b.add(level);

		p.add(b);
		// p.add(level);
		p.add(start);
		p.add(finish);
		add(p, "East");
		Panel g = new Panel();
		if (!isDesign) {
			g.setLayout(new GridLayout(3, 1, 2, 10));
		} else {
			g.setLayout(new GridLayout(2, 2, 2, 30));
		}
		go = new Button("  Go  ");
		save = new Button("Save");
		load = new Button("Load");
		clear = new Button("Clear");
		g.add(go);
		g.add(clear);

		if (isDesign) {
			g.add(save);
			g.add(load);
		}
		p.add(g);
		go.addActionListener(this);
		clear.addActionListener(this);
		save.addActionListener(this);
		load.addActionListener(this);
	}
	
	private void findSavedMaps(String folder) {
		try {
			URL test = new URL(getCodeBase(), folder);
			preset.removeAll();
			System.out.println(test);
			// System.out.println(test.getFile());
			InputStream buff = (InputStream) test.openConnection().getInputStream();
			StreamTokenizer st = new StreamTokenizer(new InputStreamReader(buff));
			int type = st.nextToken();
			int trys = 0;
			Vector<String> files = new Vector<String>();
			String last = "";
			while (type != -1 && trys < 1000) {
				// System.out.println(st.sval);
				if (st.sval != null && st.sval.endsWith(".grd") && !st.sval.equals(last)) {
					files.addElement(st.sval);
					last = st.sval;
				}
				type = st.nextToken();
				trys++;
			}
			for (int i = 0; i < files.size(); i++) {
				preset.add(files.elementAt(i).toString());
			}
			if (files.size() > 0) {
				loadPreDef();
			}

		} catch (Throwable e) {
			System.out.println(e);
		}
	}
	
	private void loadPreDef() {
		Map temp = map;
		try {
			URL load = new URL(getDocumentBase(), "Saves/" + preset.getSelectedItem());
			ObjectInputStream ois = new ObjectInputStream(load.openStream());
			remove(map);
			map = (Map) ois.readObject();
			add(map, "Center");
			map.invalidate();
			ois.close();
			System.out.println("Grid Loaded");
			invalidate();
		} catch (Throwable ex) {
			System.err.println("Load failed " + ex);
			add(temp, "Center");
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == level) {
			blocks.setState(true);
			GridCell.setEditMode(GridCell.SET_BLOCKS);
			switch (level.getSelectedIndex()) {
			case 0:
				GridCell.setNewBlockStrength(GridCell.BLOCK);
				return;
			case 1:
				GridCell.setNewBlockStrength(GridCell.VERY_TOUGH);
				return;
			case 2:
				GridCell.setNewBlockStrength(GridCell.TOUGH);
				return;
			case 3:
				GridCell.setNewBlockStrength(GridCell.NORMAL);
				return;
			case 4:
				GridCell.setNewBlockStrength(GridCell.EASY);
				return;
			default:
				GridCell.setNewBlockStrength(GridCell.NORMAL);
				return;
			}
		}
		if (e.getSource() == method) {
			switch (method.getSelectedIndex()) {
			case 0:
				finder = new HuristicAStar();
				System.out.println("Switched to Huristic A*");
				break;
			case 1:
				finder = new OneTailAStar();
				System.out.println("Switched to OneTailAStar");
				break;
			case 2:
				finder = new Fudge();
				System.out.println("Switched to version of Amit Patel's huristic");
				break;
			}
			return;
		}

		if (e.getSource() == preset) {
			// load in a file as an applet
			System.out.println("request to load " + preset.getSelectedItem());
			loadPreDef();
			return;
		}

		Checkbox box = group.getSelectedCheckbox();
		if (box == blocks) {
			GridCell.setEditMode(GridCell.SET_BLOCKS);
			return;
		}
		if (box == start) {
			GridCell.setEditMode(GridCell.SET_START);
			return;
		}
		if (box == finish) {
			GridCell.setEditMode(GridCell.SET_FINISH);
			return;
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == go) {
			finder.findPath(map);
		}else if (e.getSource() == clear) {
			GridCell.clearAll();
			map.repaint();
		}else if (e.getSource() == save) {
			if (isDesign) {
				try {
					FileDialog fd = new FileDialog(new Frame(), "Save a grid", FileDialog.SAVE);
					fd.setFile("*.grd");
					fd.setDirectory("Saves");
					fd.setFilenameFilter(new FilenameFilter() {
						public boolean accept(File f, String n) {
							System.out.println("accept requested");
							return n.endsWith("grd");
						}
					});
					fd.show();
					File file = new File(fd.getDirectory(), fd.getFile());
					FileOutputStream fos = new FileOutputStream(file);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(map);
					oos.flush();
					oos.close();
					System.out.println("Grid Saved");
				} catch (IOException ex) {
					System.err.println("Save failed " + ex);
				}
			} else {
				// saving as an applet!
				savePreDef();
			}

		}else if (e.getSource() == load) {
			try {
				FileDialog fd = new FileDialog(new Frame(), "Select Map to Load", FileDialog.LOAD);
				fd.setFile("*.grd");
				fd.setDirectory("Saves");
				fd.setFilenameFilter(new FilenameFilter() {
					public boolean accept(File f, String n) {
						System.out.println("accept requested");
						return n.endsWith("grd");
					}
				});
				fd.show();
				File file = new File(fd.getDirectory(), fd.getFile());
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				remove(map);
				map = (Map) ois.readObject();
				add(map, "Center");
				ois.close();
				System.out.println("Grid Loaded");
			} catch (Throwable ex) {
				System.err.println("Load failed " + ex);
				add(map, "Center");
			}

		}
	}

	private void savePreDef() {
		Map temp = map;
		try {
			URL save = new URL(getDocumentBase(), "User/" + System.currentTimeMillis());// gridName.getText());

			ObjectOutputStream oos = new ObjectOutputStream(save.openConnection().getOutputStream());
			oos.writeObject(map);
			oos.flush();
			oos.close();
			System.out.println("Grid Saved");
		} catch (Throwable ex) {
			System.err.println("Load failed " + ex);
			add(temp, "Center");
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(520, 420);
	}

}