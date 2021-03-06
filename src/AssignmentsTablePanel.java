import Model.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

public class AssignmentsTablePanel extends JPanel {
	private Object[] headerLabels;
	private Object[][] rows;
	private JPanel headerPanel;
	private JFrame callingFrame;
	private final Dimension buttonSize = new Dimension(100, 25);
	private final int panelWidth;
//	private final int panelWidth = 400;
//	private final Dimension panelSize;
	private final Dimension headerPanelSize;
	private final Dimension rowPanelSize;
	private Object category;
    private JTextField tf;
	public AssignmentsTablePanel(Object category, JFrame callingFrame) {
		this.headerLabels = GradingSystem.controller.getAssignmentViewHeader(category);
		this.rows = GradingSystem.controller.getAssignmentViewRows(category);
		this.panelWidth = headerLabels.length*buttonSize.width;
//		this.panelSize = new Dimension(panelWidth,(rows.length+1)*buttonSize.height);
		this.headerPanelSize = new Dimension(panelWidth, buttonSize.height + 5);
		this.rowPanelSize = new Dimension(panelWidth, buttonSize.height);
		this.callingFrame = callingFrame;
		this.category = category;
		// the line below this seems not necessary
		// ((ParentNode) this.category).genScoreTableArray(GradingSystem.controller.getRoot().getStudentPool().getPrimaryKeyAndSortBy("hello"));
//		setPreferredSize(panelSize);
		setLayout(new GridLayout(rows.length + 1, 1));
		addHeader();
		addRows();
	}

	private void addHeader() {
		headerPanel = new JPanel(new GridLayout(0, headerLabels.length));
		headerPanel.setPreferredSize(headerPanelSize);
//		headerPanel.setMinimumSize(headerPanelSize);
//		headerPanel.setMaximumSize(headerPanelSize);
		for (int i = 0; i < headerLabels.length; i++) {
			JButton button;
			// add functionality of the STUDENT header button
			if (i == 0) {
				button = new BtnStudentHeader(headerLabels[i], callingFrame, category);
			}
			// add functionality of the rest of the header buttons (the assignment title buttons)
			else {
				button = new BtnAssignmentHeader(headerLabels[i]);
			}
//			button.setSize(width, height);
			button.setPreferredSize(buttonSize);
			button.setMinimumSize(buttonSize);
			button.setMaximumSize(buttonSize);
			headerPanel.add(button);
		}
		add(headerPanel);
	}

	private void addRows() {
		for (int row = 0; row < rows.length; row++) {
			JPanel rowPanel = new JPanel(new GridLayout(0, rows[row].length));
			rowPanel.setPreferredSize(rowPanelSize);
			rowPanel.setMinimumSize(rowPanelSize);
			rowPanel.setMaximumSize(rowPanelSize);
			// set functionality of the GRADING OPTIONS row
			if (row == 0) {
				for (int col = 0; col < rows[row].length; col++) {
					// Set the label of the grading options row (it is not a clickable button)
					if (col == 0) {
						JLabel cellLabel = new JLabel(rows[row][col].toString());
//						cellLabel.setSize(width,height);
						cellLabel.setPreferredSize(buttonSize);
//						cellLabel.setMinimumSize(buttonSize);
//						cellLabel.setMaximumSize(buttonSize);
						rowPanel.add(cellLabel);
					}
					// make the clickable buttons of the grading options row, so you can click on the button and change the grading option
					else {
						JButton cellButton = new BtnAssignmentGradingOption(rows[row][col]);
//						cellButton.setSize(width,height);
						cellButton.setPreferredSize(buttonSize);
//						cellButton.setMinimumSize(buttonSize);
//						cellButton.setMaximumSize(buttonSize);
						rowPanel.add(cellButton);
					}
				}
			}
			// set functionality of the TOTAL POINTS row
			else if (row == 1) {
				for (int col = 0; col < rows[row].length; col++) {
					// Set the label of the total points row (it is not a clickable button)
					if (col == 0) {
						JLabel cellLabel = new JLabel(rows[row][col].toString());
//						cellLabel.setSize(width,height);
						cellLabel.setPreferredSize(buttonSize);
//						cellLabel.setMinimumSize(buttonSize);
//						cellLabel.setMaximumSize(buttonSize);
						rowPanel.add(cellLabel);
					}
					// make the clickable buttons of the total points row, so you can click on the button and change the grading option
					else {
						JButton cellButton = new BtnTotalPoints(rows[row][col]);
//						cellButton.setSize(width,height);
						cellButton.setPreferredSize(buttonSize);
//						cellButton.setMinimumSize(buttonSize);
//						cellButton.setMaximumSize(buttonSize);
						rowPanel.add(cellButton);
					}
				}
			}
			// set functionality of the AVERAGE row
			else if (row == 2) {
				for (int col = 0; col < rows[row].length; col++) {
					// Set the label of the average row (it is not a clickable button)
					if (col == 0) {
						JLabel cellLabel = new JLabel(rows[row][col].toString());
//						cellLabel.setSize(width,height);
						cellLabel.setPreferredSize(buttonSize);
//						cellLabel.setMinimumSize(buttonSize);
//						cellLabel.setMaximumSize(buttonSize);
						rowPanel.add(cellLabel);
					}
					// make the clickable buttons of the average row, so you can click on the button and see the summary statistics for the assignment
					else {
						JButton cellButton = new BtnAssignmentAverage(rows[row][col]);
//						cellButton.setSize(width,height);
						cellButton.setPreferredSize(buttonSize);
//						cellButton.setMinimumSize(buttonSize);
//						cellButton.setMaximumSize(buttonSize);
						rowPanel.add(cellButton);
					}
				}
			}
			// set functionality of the rest of the rows (the assignment grades rows)
			else {
				for (int col = 0; col < rows[row].length; col++) {
					// Set the clickable student information button
					JButton cellButton;
					if (col == 0) {
						cellButton = new BtnStudent(rows[row][col]);
					}
					// make the clickable assignment grade button, so you can click on a grade and edit the grade or add a note
					else {
						cellButton = new BtnAssignmentGrade(rows[row][col]);
					}
//					cellButton.setSize(width,height);
					cellButton.setPreferredSize(buttonSize);
//					cellButton.setMinimumSize(buttonSize);
//					cellButton.setMaximumSize(buttonSize);
					rowPanel.add(cellButton);
				}
			}
			add(rowPanel);
		}
	}

	public JPanel getHeaderPanel() {
		return headerPanel;
	}
}

class BtnStudentHeader extends JButton {
	BtnStudentHeader(Object student, JFrame frame, Object category){
		super(student.toString());
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StudentPool s = (StudentPool)(((Dummy) student).getRealObject());
				if (s == null) {
					loadStudentInfo();
					frame.dispose();
					if (GradingSystem.controller.getCurrentState() != GradingSystem.controller.getRoot()){
						// now we're at assignment view
						AssignmentsView assignmentsView = new AssignmentsView(GradingSystem.controller.getCurrentState());
					} else {
						// we're at class home view
						ClassHome classHome = new ClassHome();
					}
				}
				else {
					Popup_StudentInfo p = new Popup_StudentInfo (s.getDisplayOption(), frame, category);
				}
				//s.viewAllStudent();
			}
		});
	}
	BtnStudentHeader(Object student, JFrame frame) {
//		super((String) label);
		super(student.toString());
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StudentPool s = (StudentPool)(((Dummy) student).getRealObject());
				if (s == null) {
					loadStudentInfo();
					frame.dispose();
					if (GradingSystem.controller.getCurrentState() != GradingSystem.controller.getRoot()){
						// now we're at assignment view
						AssignmentsView assignmentsView = new AssignmentsView(GradingSystem.controller.getCurrentState());
					} else {
						// we're at class home view
						ClassHome classHome = new ClassHome();
					}
				}
				else {
					Popup_StudentInfo p = new Popup_StudentInfo (s.getDisplayOption(), frame);
				}
				//s.viewAllStudent();

			}
		});
	}

	private void loadStudentInfo(){
		JFileChooser jfc=new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY );
		jfc.showDialog(new JLabel(), "Choose a CSV file");
		File file=jfc.getSelectedFile();
		if(file.isFile()){
			System.out.println("File name: "+file.getAbsolutePath());
		}
		System.out.println(jfc.getSelectedFile().getName());
		String path=file.getAbsolutePath();
		StudentPool studentPool = new StudentPool();
		studentPool.importFromCsv(path);
		GradingSystem.controller.getRoot().connectStudentPool(studentPool);
	}
}

class AL_StudentHeader implements ActionListener {
	JButton callingButton;
	public AL_StudentHeader(JButton btn) {
		super();
		this.callingButton = btn;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		//Popup_StudentInfo popup_studentInfo = new Popup_StudentInfo();
	}
}

class BtnAssignmentHeader extends JButton {
	BtnAssignmentHeader(Object assignment) {
//		super((String) label);
		super(assignment.toString());
		addActionListener(new AL_AssignmentHeader(this));
	}
}

class AL_AssignmentHeader implements ActionListener {
	JButton callingButton;
	public AL_AssignmentHeader(JButton btn) {
		super();
		this.callingButton = btn;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(callingButton, "You clicked on the " + callingButton.getText() + " button, you will be able to edit the name and weight of the assignment here");
	}
}

class BtnAssignmentGradingOption extends JButton {
	BtnAssignmentGradingOption(Object label) {
//		super((String) label);
		super(label.toString());
		JButton callingBtn = this;
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Popup_GradingOption p = new Popup_GradingOption(label, callingBtn);
			}
		});
	}
}

//class AL_GradingOptions implements ActionListener {
//	JButton callingButton;
//	public AL_GradingOptions(JButton btn) {
//		super();
//		this.callingButton = btn;
//	}
//	@Override
//	public void actionPerformed(ActionEvent e) {
//		Popup_GradingOption p = new Popup_GradingOption();
//	}
//}

class BtnTotalPoints extends JButton {
	BtnTotalPoints(Object label) {
//		super((String) label);
		super(label.toString());
		JButton callingBtn = this;
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Popup_Total p = new Popup_Total(label, callingBtn);
			}
		});
	}
}

//class AL_TotalPoints implements ActionListener {
//	JButton callingButton;
//	public AL_TotalPoints(JButton btn) {
//		super();
//		this.callingButton = btn;
//	}
//	@Override
//	public void actionPerformed(ActionEvent e) {
//		//JOptionPane.showMessageDialog(callingButton, "You are now editing the total points for an assignment");
//		Popup_Total p = new Popup_Total();
//	}
//}

class BtnAssignmentAverage extends JButton {
	BtnAssignmentAverage(Object label) {
//		super((String) label);
		super(label.toString());
		//addActionListener(new AL_AssignmentAverage(this));
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Statistics stat = (Statistics) label;
				Popup_Average p = new Popup_Average(stat);
			}
		});
	}
}

//class AL_AssignmentAverage implements ActionListener {
//	JButton callingButton;
//	public AL_AssignmentAverage(JButton btn) {
//		super();
//		this.callingButton = btn;
//	}
//	@Override
//	public void actionPerformed(ActionEvent e) {
//		Popup_Average p = new Popup_Average();
//	}
//}

class BtnStudent extends JButton {
	BtnStudent(Object label) {
//		super((String) label);
		super(label.toString());
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				Student currentStudent = (Student) label;
//				currentStudent.getAllAttribute();
				Popup_Student s = new Popup_Student(label);
			}
		});
	}
}

//class AL_Student implements ActionListener {
//	JButton callingButton;
//	public AL_Student(JButton btn) {
//		super();
//		this.callingButton = btn;
//	}
//	@Override
//	public void actionPerformed(ActionEvent e) {
//		//JOptionPane.showMessageDialog(callingButton, "You clicked on a student, you will be able to see this student's information here");
//		Popup_Student p = new Popup_Student();
//	}
//}

class BtnAssignmentGrade extends JButton {
	BtnAssignmentGrade(Object grade) {
		super(grade.toString());
		boolean hasNote = ((NoteInterface) grade).hasNote();
		if (((NoteInterface) grade).hasNote()) setBackground(Color.CYAN);
		JButton callingButton = this;
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Popup_AssignmentGrade popup_assignmentGrade = new Popup_AssignmentGrade(grade, callingButton);
//				if (((NoteInterface) grade).hasNote()) setBackground(Color.CYAN);
			}
		});

	}
}

//class AL_AssignmentGrade implements ActionListener {
//	JButton callingButton;
//	public AL_AssignmentGrade(JButton btn) {
//		super();
//		this.callingButton = btn;
//	}
//	@Override
//	public void actionPerformed(ActionEvent e) {
////		JOptionPane.showMessageDialog(callingButton, "You clicked on an assignment grade, you will be able to edit the grade and add a note here");
//		Popup_AssignmentGrade popup_assignmentGrade = new Popup_AssignmentGrade()
//	}
//}