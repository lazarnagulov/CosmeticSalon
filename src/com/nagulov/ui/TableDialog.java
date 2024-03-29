package com.nagulov.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowFilter.ComparisonType;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import com.nagulov.controllers.ServiceController;
import com.nagulov.controllers.TreatmentController;
import com.nagulov.controllers.UserController;
import com.nagulov.data.DataBase;
import com.nagulov.data.ErrorMessage;
import com.nagulov.treatments.CosmeticService;
import com.nagulov.treatments.CosmeticTreatment;
import com.nagulov.treatments.TreatmentStatus;
import com.nagulov.ui.models.BeauticianIncomeModel;
import com.nagulov.ui.models.CosmeticTreatmentModel;
import com.nagulov.ui.models.LoyalityCardModel;
import com.nagulov.ui.models.ServiceModel;
import com.nagulov.ui.models.TimeTableModel;
import com.nagulov.ui.models.TreatmentModel;
import com.nagulov.ui.models.TreatmentStatusModel;
import com.nagulov.ui.models.UserModel;
import com.nagulov.users.Beautician;
import com.nagulov.users.Client;
import com.nagulov.users.Receptionist;
import com.nagulov.users.User;

import net.miginfocom.swing.MigLayout;

public class TableDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JToolBar toolbar;
	private static JTable table;
	
	private JButton addButton = new JButton();
	private JButton editButton = new JButton();
	private JButton removeButton = new JButton();
	
	private ImageIcon addIcon = new ImageIcon("img/add.gif");
	private ImageIcon editIcon = new ImageIcon("img/edit.gif");
	private ImageIcon removeIcon = new ImageIcon("img/remove.gif");

	private JPanel filterPanel = new JPanel();
	private JTextField filterCosmeticTreatment = new JTextField(20);
	private JTextField filterCosmeticService = new JTextField(20);
	private JTextField filterStartingPrice = new JTextField(20);
	private JTextField filterEndingPrice = new JTextField(20);
	private JButton filterButton = new JButton("Filter");
	
	private TableRowSorter<AbstractTableModel> sorter = new TableRowSorter<AbstractTableModel>();
	
	public static void refreshUser() {
		UserModel model = (UserModel)table.getModel();
		model.fireTableDataChanged();
	}
	
	public static void refreshService() {
		ServiceModel model = (ServiceModel)table.getModel();
		model.fireTableDataChanged();
	}
	
	public static void refreshTreatment() {
		TreatmentModel model = (TreatmentModel)table.getModel();
		model.fireTableDataChanged();
	}
	
	private void initUserModel() {
		this.setTitle("Users");
		table = new JTable(new UserModel());
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		init();
		
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new RegisterDialog(true);
			}
		});
		
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if(row == -1) {
					JOptionPane.showMessageDialog(null, ErrorMessage.ROW_NOT_SELECTED.getError(), "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String username = table.getValueAt(row, 1).toString();
				User u = UserController.getInstance().getUser(username);
				new EditUserDialog(u);
				refreshUser();
			}
		});
		
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if(row == -1) {
					JOptionPane.showMessageDialog(null, ErrorMessage.ROW_NOT_SELECTED.getError(), "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String username = table.getValueAt(row, 1).toString();
				User u = UserController.getInstance().getUser(username);
				int choice = JOptionPane.showConfirmDialog(null,"Are you sure you want to delete", 
						u.getName() + " "+u.getSurname() +" - Confirm", JOptionPane.YES_NO_OPTION);
				if(choice == JOptionPane.OK_OPTION) {
					UserController.getInstance().removeUser(u);
					UserModel.removeUser(row);
					refreshUser();
				}
			}
			
		});
		
	}
		
	private void initServiceModel() {
		this.setTitle("Services");	
		table = new JTable(new ServiceModel());
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		init();
		
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AddServiceDialog();
			}
		});
		
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if(row == -1) {
					JOptionPane.showMessageDialog(null, ErrorMessage.ROW_NOT_SELECTED.getError(), "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				CosmeticService service = ServiceController.getInstance().getServices().get(table.getValueAt(row, 0).toString());
				new EditServiceDialog(service, service.getTreatment(table.getValueAt(row, 1).toString()), row);
			}
		});
		
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if(row == -1) {
					new RemoveServiceDialog();
					return;
				}
				String serviceName = table.getValueAt(row, 0).toString();
				String treatmentName = table.getValueAt(row, 1).toString();
				CosmeticService service = ServiceController.getInstance().getServices().get(serviceName);
				int choice = JOptionPane.showConfirmDialog(null,"Are you sure you want to delete " + treatmentName + "?", "Confirm", JOptionPane.YES_NO_OPTION);
				if(choice == JOptionPane.OK_OPTION) {
					service.removeTreatment(treatmentName);
					ServiceModel.removeService(row);
					refreshService();
				}
			}
		});
	}
	
	private void initTreatmentModel(){
		this.setTitle("Treatments");	
		table = new JTable(new TreatmentModel());
		sorter.setModel((AbstractTableModel) table.getModel());
		table.setRowSorter(sorter);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		for(int i=0; i<7; i++) {
			sorter.setSortable(i, false);
		}
		init();
		
		
		filterPanel.setLayout(new MigLayout("wrap 9", "[][][][][][][][][]", "[]"));
		filterPanel.add(new JLabel("Service"));
		filterPanel.add(filterCosmeticService);
		filterPanel.add(new JLabel("Treatment:"));
		filterPanel.add(filterCosmeticTreatment);
		filterPanel.add(new JLabel("Min price:"));
		filterPanel.add(filterStartingPrice);
		filterPanel.add(new JLabel("Max price:"));
		filterPanel.add(filterEndingPrice);
		filterPanel.add(filterButton);

		this.getContentPane().add(filterPanel, BorderLayout.SOUTH);
		
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new EditTreatmentDialog();
			}
		});
		
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if(row == -1) {
					JOptionPane.showMessageDialog(null, ErrorMessage.ROW_NOT_SELECTED.getError(), "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				new EditTreatmentDialog(TreatmentModel.getTreatment(row));
			}
		});	
		
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if(row == -1) {
					JOptionPane.showMessageDialog(null, ErrorMessage.ROW_NOT_SELECTED.getError(), "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				int treatmentId = TreatmentModel.getTreatment(row).getId();
				int choice = JOptionPane.showConfirmDialog(null,"Are you sure you want to delete", "Confirm", JOptionPane.YES_NO_OPTION);
				if(choice == JOptionPane.OK_OPTION) {
					TreatmentController.getInstance().removeTreatment(treatmentId);
					TreatmentModel.removeTreatment(row);
					refreshTreatment();
				}
			}
		});
		
		filterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String service = filterCosmeticService.getText();
				String treatment = filterCosmeticTreatment.getText();
				String startingPrice = filterStartingPrice.getText();
				String endingPrice = filterEndingPrice.getText();
				List<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>();
				if(!service.equals("")) {
					filters.add(RowFilter.regexFilter("(?i).*" + service + "*.", 1));
				}
				if(!treatment.equals("")) {
					filters.add(RowFilter.regexFilter("(?i).*" + treatment + "*.", 2));
				}
				if(!startingPrice.equals("")) {
					filters.add(RowFilter.numberFilter(ComparisonType.AFTER, Double.valueOf(startingPrice) - 10e-10, 6));
				}
				if(!endingPrice.equals("")) {
					filters.add(RowFilter.numberFilter(ComparisonType.BEFORE, Double.valueOf(endingPrice) + 10e-10, 6));
				}
				RowFilter<Object, Object> filter = RowFilter.andFilter(filters);
				sorter.setRowFilter(filter);
			}
		});
	}
	
	private void initBeauticianIncome() {
		this.setTitle("Beautician income");	
		table = new JTable(new BeauticianIncomeModel());
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		this.setSize(500,300);
		this.setLocationRelativeTo(null);
		this.setIconImage(new ImageIcon("img" + DataBase.SEPARATOR + "logo.jpg").getImage());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.setLocationRelativeTo(null);
		JScrollPane sc = new JScrollPane(table);
		add(sc, BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
	private void initLoyalityCard() {
		this.setTitle("Loyality card");	
		this.setIconImage(new ImageIcon("img" + DataBase.SEPARATOR + "logo.jpg").getImage());
		table = new JTable(new LoyalityCardModel());
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		this.setSize(800,300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.setLocationRelativeTo(null);
		JScrollPane sc = new JScrollPane(table);
		add(sc, BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
	private void initBeauticianTreatment() {
		this.setTitle("Treatments");	
		table = new JTable(new TreatmentModel());
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		this.setSize(800,300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setIconImage(new ImageIcon("img" + DataBase.SEPARATOR + "logo.jpg").getImage());
		
		JButton performTreatment = new JButton("Perform treatment");
		
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(new MigLayout("wrap", "[grow,fill]", "[center]20[grow,fill][]"));
		JScrollPane sc = new JScrollPane(table);
		this.getContentPane().add(new JLabel("Treatments list:"), "center");
		this.getContentPane().add(sc, "center");
		this.getContentPane().add(performTreatment, "center");
		
		this.setVisible(true);
	
		performTreatment.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if(row == -1) {
					JOptionPane.showMessageDialog(null, ErrorMessage.ROW_NOT_SELECTED.getError(), "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				int treatmentId = TreatmentModel.getTreatment(row).getId();
				TreatmentStatus status = TreatmentController.getInstance().getTreatment(treatmentId).getStatus();
				if(status.equals(TreatmentStatus.SCHEDULED)) {
					TreatmentController.getInstance().getTreatment(treatmentId).setStatus(TreatmentStatus.PERFORMED);
					refreshTreatment();
				}else {
					JOptionPane.showMessageDialog(null, ErrorMessage.CANNOT_PERFORM.getError(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
	
	private void initClientTreatment() {
		this.setTitle("Treatments");	
		table = new JTable(new TreatmentModel());
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		this.setIconImage(new ImageIcon("img" + DataBase.SEPARATOR + "logo.jpg").getImage());
		this.setSize(800,300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JButton cancelTreatment = new JButton("Cancel treatment");
		
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(new MigLayout("wrap", "[grow,fill]", "[]20[grow,fill][]"));
		JScrollPane sc = new JScrollPane(table);
		this.getContentPane().add(new JLabel("Treatments list:"), "center");
		this.getContentPane().add(sc, "center");
		this.getContentPane().add(cancelTreatment, "center");
		
		this.setVisible(true);
	
		cancelTreatment.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if(row == -1) {
					JOptionPane.showMessageDialog(null, ErrorMessage.ROW_NOT_SELECTED.getError(), "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				int treatmentId = TreatmentModel.getTreatment(row).getId(); 
				TreatmentStatus status = TreatmentController.getInstance().getTreatment(treatmentId).getStatus();
				if(status.equals(TreatmentStatus.SCHEDULED)) {
					int choice = JOptionPane.showConfirmDialog(null,"Are you sure you want to cancel treatment?", "Confirm", JOptionPane.YES_NO_OPTION);
					if(choice == JOptionPane.OK_OPTION) {	
						TreatmentController.getInstance().getTreatment(treatmentId).setStatus(TreatmentStatus.CANCELED_BY_THE_CLIENT);
						refreshTreatment();
					}
				}else {
					JOptionPane.showMessageDialog(null, ErrorMessage.CANNOT_CANCEL.getError(), "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});
	}
	
	private void initTimeTable() {
		this.setTitle("Timetable");	
		table = new JTable(new TimeTableModel());
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		this.setIconImage(new ImageIcon("img" + DataBase.SEPARATOR + "logo.jpg").getImage());
		JScrollPane sc = new JScrollPane(table);
		this.setSize(800,300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		this.getContentPane().add(sc, BorderLayout.CENTER);
		
		this.setVisible(true);
	
	}
	
	private void initTreatmentStatus() {
		this.setTitle("Treatment report");	
		table = new JTable(new TreatmentStatusModel());
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		this.setIconImage(new ImageIcon("img" + DataBase.SEPARATOR + "logo.jpg").getImage());
		this.setSize(800,300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		
		this.setLocationRelativeTo(null);
		JScrollPane sc = new JScrollPane(table);
		this.getContentPane().add(sc, BorderLayout.CENTER);
		
		this.setVisible(true);
		
	}
	
	private void initCosmeticTreatment(CosmeticTreatment treatment) {
		this.setTitle("Cosmetic Treatment report");	
		table = new JTable(new CosmeticTreatmentModel());
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		this.setIconImage(new ImageIcon("img" + DataBase.SEPARATOR + "logo.jpg").getImage());
		this.setSize(300,300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.getContentPane().setLayout(new MigLayout("wrap", "[grow,fill]", "[]20[150px]"));

		
		JPanel info = new JPanel();
		info.setLayout(new MigLayout("wrap", "[grow,fill]", "[]20[grow,fill][grow,fill][grow,fill]"));
		info.add(new JLabel("Cosmetic Treatment info:"), "span 2");
		info.add(new JLabel("Service: " + ServiceController.getInstance().getCosmeticTreatments().get(treatment).getName()));
		info.add(new JLabel("Name: " + treatment.getName()));
		info.add(new JLabel("Duration: " + treatment.getDuration().format(DataBase.TIME_FORMAT)));
		
		this.setLocationRelativeTo(null);
		JScrollPane sc = new JScrollPane(table);
		
		this.getContentPane().add(info, "span 2, center");
		this.getContentPane().add(sc, BorderLayout.CENTER);
		
		this.setVisible(true);
		
	}
	
	private void init() {
		this.setSize(800,300);
		this.setIconImage(new ImageIcon("img" + DataBase.SEPARATOR + "logo.jpg").getImage());
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.getContentPane().add(addButton);
		this.getContentPane().add(editButton);
		this.getContentPane().add(removeButton);
		
		addButton.setIcon(addIcon);
		editButton.setIcon(editIcon);
		toolbar = new JToolBar();
		toolbar.add(addButton);
		toolbar.add(editButton);
		
		if(!(DataBase.loggedUser instanceof Receptionist)) {
			removeButton.setIcon(removeIcon);
			toolbar.add(removeButton);
		}
		
		toolbar.setFloatable(false);		

		this.getContentPane().add(toolbar, BorderLayout.NORTH);
		JScrollPane sc = new JScrollPane(table);
		add(sc, BorderLayout.CENTER);
		this.setVisible(true);
	}
	
	
	
	
	public TableDialog(Table table) {
		switch(table) {
			case USER:
				UserModel.init();
				initUserModel();
				break;
			case SERVICE:
				ServiceModel.init();
				initServiceModel();
				break;
			case TREATMENT:
				TreatmentModel.init();
				initTreatmentModel();
				break;
			case BEAUTICIAN_INCOME:
				BeauticianIncomeModel.init();
				initBeauticianIncome();
				break;
			case LOYALITY_CARD:
				LoyalityCardModel.init();
				initLoyalityCard();
				break;
			case TREATMENTS_STATUS:
				TreatmentStatusModel.init();
				initTreatmentStatus();
				break;
			case COSMETIC_TREATMENT_STATUS:
			case TIMETABLE:
			default:
				break;
		}
		
	}
	
	

	public TableDialog(Table table, User user) {
		if(table.equals(Table.TREATMENT)) {
			if(user instanceof Client) {
				TreatmentModel.init((Client)user);
				initClientTreatment();
				return;
			}else if(user instanceof Beautician) {
				TreatmentModel.init((Beautician)user);
				initBeauticianTreatment();
				return;
			}
		}
		
		if(table.equals(Table.TIMETABLE)) {
			if(user instanceof Beautician) {
				TimeTableModel.init((Beautician)user);
				initTimeTable();
				return;
			}
		}
	}
	
	public TableDialog(CosmeticTreatment treatment) {
		CosmeticTreatmentModel.init();
		initCosmeticTreatment(treatment);
	}
}
