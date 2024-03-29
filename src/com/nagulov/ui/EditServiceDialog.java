package com.nagulov.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.nagulov.controllers.ServiceController;
import com.nagulov.data.DataBase;
import com.nagulov.data.ErrorMessage;
import com.nagulov.treatments.CosmeticService;
import com.nagulov.treatments.CosmeticTreatment;
import com.nagulov.treatments.Pricelist;
import com.nagulov.treatments.Salon;
import com.nagulov.ui.models.ServiceModel;

import net.miginfocom.swing.MigLayout;

public class EditServiceDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CosmeticService service;
	private CosmeticTreatment treatment;
	private int row;
	private JComboBox<String> serviceField;
	private JTextField treatmentField = new JTextField(20);
	private JTextField durationField = new JTextField(20);
	private JTextField priceField = new JTextField(20);
	
	private JButton confirmButton = new JButton("Confirm");
	private JButton cancelButton = new JButton("Cancel");

	private void initEditServiceDialog() {
		
		serviceField = new JComboBox<String>();
		for(Map.Entry<String, CosmeticService> entry : ServiceController.getInstance().getServices().entrySet()) {	
			serviceField.addItem(entry.getKey());
			if(entry.getKey().equals(service.getName())) {
				serviceField.setSelectedItem(entry.getKey());
			}
		}
		
		treatmentField.setText(treatment.getName());
		durationField.setText(treatment.getDuration().toString());
		Double price = Pricelist.getInstance().getPrice(treatment);
		priceField.setText(price.toString());
		
		this.getContentPane().setLayout(new MigLayout("wrap 2", "[][]", "[]20[][][][]20[]"));
		
		this.getContentPane().add(new JLabel("Edit service"), "span 2");
		this.getContentPane().add(new JLabel("Service"));
		this.getContentPane().add(serviceField);
		this.getContentPane().add(new JLabel("Treatment"));
		this.getContentPane().add(treatmentField);
		this.getContentPane().add(new JLabel("Duration"));
		this.getContentPane().add(durationField);
		this.getContentPane().add(new JLabel("Price"));
		this.getContentPane().add(priceField);
		this.add(cancelButton);
		this.add(confirmButton);
		
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String newService = serviceField.getSelectedItem().toString();
				String newTreatment = treatmentField.getText();
				LocalTime duration = null;
				double price = 0.0;
				try {
					duration = LocalTime.parse(durationField.getText());
					price = Double.parseDouble(priceField.getText());
				}catch(Exception invalidInput) {
					JOptionPane.showMessageDialog(null, ErrorMessage.INVALID_INPUT.getError(), "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				CosmeticService cs = ServiceController.getInstance().getServices().get(newService);
				if(!cs.equals(service)) {
					service.removeTreatment(treatment);
					cs.addTreatment(treatment);
				}
				
				ServiceController.getInstance().updateCosmeticTreatment(treatment, newTreatment, duration, price);
				ServiceModel.updateService(row, newService, newTreatment, durationField.getText(), priceField.getText());
				TableDialog.refreshService();
				
				setVisible(false);
				dispose();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
	
	}
	
	public EditServiceDialog(CosmeticService service, CosmeticTreatment treatment, int row) {
		this.service = service;
		this.treatment = treatment;
		this.row = row;
		setTitle(Salon.getInstance().getSalonName());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setIconImage(new ImageIcon("img" + DataBase.SEPARATOR + "logo.jpg").getImage());
		this.setLocationRelativeTo(null);
		initEditServiceDialog();
		pack();
		setVisible(true);
	}

}
