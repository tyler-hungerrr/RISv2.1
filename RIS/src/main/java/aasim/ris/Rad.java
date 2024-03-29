package aasim.ris;

import static aasim.ris.App.ds;
import static aasim.ris.App.url;
import datastorage.Appointment;
import datastorage.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.postgresql.ds.PGSimpleDataSource;

public class Rad extends Stage {
    //navbar

    HBox navbar = new HBox();
    Label username = new Label("Logged In as Radiologist: " + App.user.getFullName());
    ImageView pfp = new ImageView(App.user.getPfp());

    Button logOut = new Button("Log Out");

    //end navbar
    //table
    TableView appointmentsTable = new TableView();
    VBox tableContainer = new VBox(appointmentsTable);

    //scene
    BorderPane main = new BorderPane();
    Scene scene = new Scene(main);

    //end scene
    private FilteredList<Appointment> flAppointment;
    ChoiceBox<String> choiceBox = new ChoiceBox();
    TextField search = new TextField("Search Appointments");
    HBox searchContainer = new HBox(choiceBox, search);

    private final FileChooser fileChooser = new FileChooser();

    public Rad() {
        this.setTitle("RIS - Radiology Information System (Radiologist)");
        //navbar
        navbar.setAlignment(Pos.TOP_RIGHT);
        logOut.setPrefHeight(30);
        logOut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                logOut();
            }
        });
        pfp.setPreserveRatio(true);
        pfp.setFitHeight(38);
        username.setId("navbar");
        username.setOnMouseClicked(eh -> userInfo());
        navbar.getChildren().addAll(username, pfp, logOut);
        navbar.setStyle("-fx-background-color: #2f4f4f; -fx-spacing: 15;");
        main.setTop(navbar);
        //end navbar

        //center
        main.setCenter(tableContainer);
        createTableAppointments();
        populateTable();
        //end center

        //Searchbar Structure
        tableContainer.getChildren().add(searchContainer);
        searchContainer.setAlignment(Pos.TOP_RIGHT);
        HBox.setHgrow(searchContainer, Priority.ALWAYS);
        choiceBox.setPrefHeight(40);
        search.setPrefHeight(40);
        choiceBox.getItems().addAll("Patient ID", "Full Name", "Date/Time", "Order", "Status");
        choiceBox.setValue("Patient ID");
        search.textProperty().addListener((obs, oldValue, newValue) -> {
            if (choiceBox.getValue().equals("Patient ID")) {
                flAppointment.setPredicate(p -> new String(p.getPatientID() + "").contains(newValue));//filter table by Patient Id
            } else if (choiceBox.getValue().equals("Full Name")) {
                flAppointment.setPredicate(p -> p.getFullName().toLowerCase().contains(newValue.toLowerCase()));//filter table by Full name
            } else if (choiceBox.getValue().equals("Date/Time")) {
                flAppointment.setPredicate(p -> p.getTime().contains(newValue));//filter table by Date/Time
            } else if (choiceBox.getValue().equals("Order")) {
                flAppointment.setPredicate(p -> p.getOrder().toLowerCase().contains(newValue.toLowerCase()));//filter table by Date/Time
            } else if (choiceBox.getValue().equals("Status")) {
                flAppointment.setPredicate(p -> p.getStatus().toLowerCase().contains(newValue.toLowerCase()));//filter table by Status
            }
            appointmentsTable.getItems().clear();
            appointmentsTable.getItems().addAll(flAppointment);
        });

        //set scene and structure
        scene.getStylesheets().add("file:stylesheet.css");
        this.setScene(scene);
    }

    private void createTableAppointments() {
        //columns of table
        TableColumn patientIDCol = new TableColumn("Patient ID");
        TableColumn fullNameCol = new TableColumn("Name");
        TableColumn timeCol = new TableColumn("Time");
        TableColumn orderIDCol = new TableColumn("Orders");
        TableColumn statusCol = new TableColumn("Appointment Status");
        TableColumn reportCol = new TableColumn("Report");
        TableColumn reportCol2 = new TableColumn("Edit Report");

        //all of the value settings
        patientIDCol.setCellValueFactory(new PropertyValueFactory<>("patientID"));
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        orderIDCol.setCellValueFactory(new PropertyValueFactory<>("order"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("statusAsLabel"));
        reportCol.setCellValueFactory(new PropertyValueFactory<>("placeholder"));
        reportCol2.setCellValueFactory(new PropertyValueFactory<>("placeholder1"));

        //Couldn't put all the styling
        patientIDCol.prefWidthProperty().bind(appointmentsTable.widthProperty().multiply(0.09));
        fullNameCol.prefWidthProperty().bind(appointmentsTable.widthProperty().multiply(0.06));
        timeCol.prefWidthProperty().bind(appointmentsTable.widthProperty().multiply(0.2));
        orderIDCol.prefWidthProperty().bind(appointmentsTable.widthProperty().multiply(0.3));
        statusCol.prefWidthProperty().bind(appointmentsTable.widthProperty().multiply(0.1));
        reportCol2.prefWidthProperty().bind(appointmentsTable.widthProperty().multiply(0.05));
        appointmentsTable.setStyle("-fx-background-color: #25A18E; -fx-text-fill: WHITE; ");
        //Together again
        appointmentsTable.getColumns().addAll(patientIDCol, fullNameCol, timeCol, orderIDCol, statusCol, reportCol, reportCol2);
        //Add Status Update Column:
    }

    private void populateTable() {
        appointmentsTable.getItems().clear();
        //connects to database

        String sql = "Select appt_id, patient_id, patients.full_name, time, statusCode.status"
                + " FROM appointments"
                + " INNER JOIN statusCode ON appointments.statusCode = statusCode.statusID"
                + " INNER JOIN patients ON appointments.patient_id = patients.patientID"
                + " WHERE statusCode BETWEEN 4 AND 6"
                + " ORDER BY time ASC;";
        try {

            Connection conn = ds.getConnection();

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            //
            List<Appointment> list = new ArrayList<Appointment>();

            while (rs.next()) {
                //What I receieve:  apptId, patientID, fullname, time, address, insurance, referral, status, order
                Appointment appt = new Appointment(rs.getString("appt_id"), rs.getString("patient_id"), rs.getString("time"), rs.getString("status"), getPatOrders(rs.getString("patient_id"), rs.getString("appt_id")), rs.getString("time"));
                appt.setFullName(rs.getString("full_name"));
                    
                appt.placeholder.setText("Create Report");
                appt.placeholder.setId("complete");
                appt.placeholder.setOnAction(eh -> radPageTwo(appt.getPatientID(), appt.getApptID(), appt.getFullName(), appt.getOrder()));
                
                list.add(appt);
            }
            for (Appointment z : list) {
                z.placeholder1.setText("Edit Report");
                z.placeholder1.setId("cancel");
                z.placeholder1.setOnAction(eh -> radPageThree(z.getPatientID(), z.getApptID(), z.getFullName(), z.getOrder()));
            }
            flAppointment = new FilteredList(FXCollections.observableList(list), p -> true);
            appointmentsTable.getItems().addAll(flAppointment);
            //
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private String getPatOrders(String patientID, String aInt) {

        String sql = "Select orderCodes.orders "
                + " FROM appointmentsOrdersConnector "
                + " INNER JOIN orderCodes ON appointmentsOrdersConnector.orderCodeID = orderCodes.orderID "
                + " WHERE apptID = '" + aInt + "';";

        String value = "";
        try {
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            //

            while (rs.next()) {

                value += rs.getString("orders") + ", ";
            }
            //
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return value;
    }

    private void logOut() {
        App.user = new User();
        Stage x = new Login();
        x.show();
        x.setMaximized(true);
        this.close();
    }

    private void userInfo() {
        Stage x = new UserInfo();
        x.show();
        x.setMaximized(true);
        this.close();
    }

    private void radOne() {
        populateTable();
        main.setCenter(tableContainer);
    }
    //creates a new report
    private void radPageTwo(String patID, String apptId, String fullname, String order) {
        VBox container = new VBox();
        container.setSpacing(10);
        container.setAlignment(Pos.CENTER);
        Label patInfo = new Label("Patient: " + fullname + "\t Order/s Requested: " + order + "\n");
        Label imgInfo = new Label("Images Uploaded: " + fullname + "\t Order/s Requested: " + order);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png")
        //                 new FileChooser.ExtensionFilter("HTML Files", "*.htm")
        );
//        Button complete = new Button("Fulfill Order");
//        complete.setId("complete");
        Button cancel = new Button("Go Back");
        cancel.setId("cancel");
        Button addReport = new Button("Upload Report");
        HBox buttonContainer = new HBox(cancel, addReport);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setSpacing(25);
        container.getChildren().addAll(patInfo, buttonContainer);
        main.setCenter(container);
        //Set Size of Every button in buttonContainer
        cancel.setPrefSize(200, 100);
        addReport.setPrefSize(200, 100);
        //
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                populateTable();
                main.setCenter(tableContainer);
            }
        });

        addReport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                openFile(patID, apptId);

            }
        });

    }
    //updates an existing report
    private void radPageThree(String patID, String apptId, String fullname, String order) {
        VBox container = new VBox();
        container.setSpacing(10);
        container.setAlignment(Pos.CENTER);
        Label patInfo = new Label("Patient: " + fullname + "\t Order/s Requested: " + order + "\n");
        Label imgInfo = new Label("Images Uploaded: " + fullname + "\t Order/s Requested: " + order);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png")
        );

        Button cancel = new Button("Go Back");
        cancel.setId("cancel");
        Button editReport = new Button("Edit Report");
        HBox buttonContainer = new HBox(cancel, editReport);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setSpacing(25);
        container.getChildren().addAll(patInfo, buttonContainer);
        main.setCenter(container);
        //Set Size of Every button in buttonContainer
        cancel.setPrefSize(200, 100);
        editReport.setPrefSize(200, 100);
        //
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                populateTable();
                main.setCenter(tableContainer);
            }
        });

        editReport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                editFile(patID, apptId);

            }
        });
        
    }
    //creates a new report
    private void openFile(String patID, String apptId) {

        Stage x = new Stage();
        x.initOwner(this);
        x.initModality(Modality.WINDOW_MODAL);
        x.setMaximized(true);
        BorderPane y = new BorderPane();
        Label label = new Label("Report");
        Button confirm = new Button("Confirm");
        confirm.setId("complete");
        // Button viewImg = new Button("View Image");
        //viewImg.setId("View Image");

        VBox imgContainer = new VBox();
        ArrayList<Pair> list = retrieveUploadedImages(apptId);
        ArrayList<HBox> hbox = new ArrayList<HBox>();
        int counter = 0;
        int hboxCounter = 0;
        if (list.isEmpty()) {
            System.out.println("Error, image list is empty");
        } else {
            for (int i = 0; i < (list.size() / 2) + 1; i++) {
                hbox.add(new HBox());
            }
            for (Pair i : list) {
                if (counter > 2) {
                    counter++;
                    hboxCounter++;
                }
                ImageView temp = new ImageView(i.getImg());
                temp.setPreserveRatio(true);
                temp.setFitHeight(300);
                Button download = new Button("Download");
                VBox tempBox = new VBox(temp, download);
                tempBox.setId("borderOnHover");
                tempBox.setSpacing(5);
                tempBox.setAlignment(Pos.CENTER);
                tempBox.setPadding(new Insets(10));
                hbox.get(hboxCounter).getChildren().addAll(tempBox);
                download.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        DirectoryChooser directoryChooser = new DirectoryChooser();
                        File selectedDirectory = directoryChooser.showDialog(x);
                        downloadImage(i, selectedDirectory);
                    }

                });
                counter++;
            }
        }
        for (HBox i : hbox) {
            imgContainer.getChildren().add(i);
        }
        imgContainer.setSpacing(10);
        imgContainer.setPadding(new Insets(10));
        ScrollPane s1 = new ScrollPane();
        s1.setContent(imgContainer);

        TextArea reportText = new TextArea();
        reportText.getText();

        Button cancel = new Button("Cancel");
        cancel.setId("cancel");
        HBox btnContainer = new HBox(cancel, confirm, s1);
        btnContainer.setSpacing(25);
        y.getStylesheets().add("file:stylesheet.css");
        x.setScene(new Scene(y));

        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                x.close();
            }
        });
        confirm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (reportText.getText().isBlank()) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Error");
                    a.setHeaderText("Try Again");
                    a.setContentText("Please enter a valid report.\n");
                    a.show();
                    return;
                }
                addReportToDatabase(reportText.getText(), apptId);
                updateAppointmentStatus(patID, apptId);
                x.close();
                populateTable();
                main.setCenter(tableContainer);
            }
        });

        VBox container = new VBox(s1, label, reportText, btnContainer);
        y.setCenter(container);
        x.show();
    }
    //updates an existing report
    private void editFile(String patID, String apptId) {

        Stage x = new Stage();
        x.initOwner(this);
        x.initModality(Modality.WINDOW_MODAL);
        x.setMaximized(true);
        BorderPane y = new BorderPane();
        Label label = new Label("Report");
        Button confirm = new Button("Confirm");
        confirm.setId("complete");

        VBox imgContainer = new VBox();
        ArrayList<Pair> list = retrieveUploadedImages(apptId);
        ArrayList<HBox> hbox = new ArrayList<HBox>();
        int counter = 0;
        int hboxCounter = 0;
        if (list.isEmpty()) {
            System.out.println("Error, image list is empty");
        } else {
            for (int i = 0; i < (list.size() / 2) + 1; i++) {
                hbox.add(new HBox());
            }
            for (Pair i : list) {
                if (counter > 2) {
                    counter++;
                    hboxCounter++;
                }
                ImageView temp = new ImageView(i.getImg());
                temp.setPreserveRatio(true);
                temp.setFitHeight(300);
                Button download = new Button("Download");
                VBox tempBox = new VBox(temp, download);
                tempBox.setId("borderOnHover");
                tempBox.setSpacing(5);
                tempBox.setAlignment(Pos.CENTER);
                tempBox.setPadding(new Insets(10));
                hbox.get(hboxCounter).getChildren().addAll(tempBox);
                download.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        DirectoryChooser directoryChooser = new DirectoryChooser();
                        File selectedDirectory = directoryChooser.showDialog(x);
                        downloadImage(i, selectedDirectory);
                    }

                });
                counter++;
            }
        }
        for (HBox i : hbox) {
            imgContainer.getChildren().add(i);
        }
        imgContainer.setSpacing(10);
        imgContainer.setPadding(new Insets(10));
        ScrollPane s1 = new ScrollPane();
        s1.setContent(imgContainer);

        TextArea reportText = new TextArea();
        reportText.setText("Radiology Report: \n" + getRadiologyReport(apptId) + "\n\n");

        Button cancel = new Button("Cancel");
        cancel.setId("cancel");
        HBox btnContainer = new HBox(cancel, confirm, s1);
        btnContainer.setSpacing(25);
        y.getStylesheets().add("file:stylesheet.css");
        x.setScene(new Scene(y));

        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                x.close();
            }
        });
        confirm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (reportText.getText().isBlank()) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Error");
                    a.setHeaderText("Try Again");
                    a.setContentText("Please enter a valid report.\n");
                    a.show();
                    return;
                }
                editReportOnDatabase(reportText.getText(), apptId);
                updateAppointmentStatus1(patID, apptId);
                x.close();
                populateTable();
                main.setCenter(tableContainer);
            }
        });

        VBox container = new VBox(s1, label, reportText, btnContainer);
        y.setCenter(container);
        x.show();
    }
    //gets the written report from database
    private String getRadiologyReport(String apptID) {
        String value = "";

        String sql = "SELECT writtenReport "
                + " FROM report"
                + " WHERE apptID = '" + apptID + "';";
        try {

            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            //
            while (rs.next()) {
                //What I receieve:  text
                value = rs.getString("writtenReport");
            }
            //
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return value;
    }
    //adds the written report to the database
    private void addReportToDatabase(String report, String apptId) {
        String sql = "INSERT INTO report (apptID, writtenreport) VALUES ('" + apptId + "', ?);";
        try {
            Connection conn = ds.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, report);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    //overrides the existing report on the database
    private void editReportOnDatabase(String report, String apptId) {
        String sql = "UPDATE report "
                + " SET writtenReport  = '" + report + "' "
                + " WHERE apptId = '" + apptId + "';";
        App.executeSQLStatement(sql);
    }

    private ArrayList<Pair> retrieveUploadedImages(String apptId) {
        //Connect to database
        ArrayList<Pair> list = new ArrayList<Pair>();

        String sql = "SELECT *"
                + " FROM images"
                + " WHERE apptID = '" + apptId + "'"
                + " ORDER BY imageID DESC;";

        try {
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            //
            while (rs.next()) {
                //What I receieve:  image
                Pair pair = new Pair(new Image(rs.getBinaryStream("image")), rs.getString("imageID"));
                pair.fis = rs.getBinaryStream("image");
                list.add(pair);
//                System.out.println(rs.getBinaryStream("image"));
            }
            //
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    private void updateAppointmentStatus(String patID, String apptId) {
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        String time1 = String.valueOf(date);
        
        String sql = "Select radtime"
                + " FROM perfevel"
                + " "
                + " WHERE appt_jd  = '" + apptId + "'"
                + ";";
        
        Integer calc = 0;
        String time = "";
        
        try {

            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            //
            

            while (rs.next()) {
                time = rs.getString("radtime");
                String time1_1 = time.substring(0, 4);
                String time1_2 = time.substring(5, 7);
                String time1_3 = time.substring(8, 10);
                String time2_1 = time1.substring(0, 3);
                String time2_2 = time1.substring(5, 6);
                String time2_3 = time1.substring(8, 9);
                Integer Time1_1 = Integer.valueOf(time1_1);
                Integer Time1_2 = Integer.valueOf(time1_2);
                Integer Time1_3 = Integer.valueOf(time1_3);
                Integer Time2_1 = Integer.valueOf(time2_1);
                Integer Time2_2 = Integer.valueOf(time2_2);
                Integer Time2_3 = Integer.valueOf(time2_3);
                Integer calc1 = Time2_1 - Time1_1;
                Integer calc2 = Time2_2 - Time1_2;
                Integer calc3 = Time2_3 - Time1_3;
                Integer gcalc = 0;
                Integer hcalc = 0;
                Integer jcalc = 0;
                if(!Time2_1.equals(Time1_1)) {
                    gcalc = gcalc + calc1;
                    gcalc = gcalc * 365;
                }
                if(!Time2_2.equals(Time2_2)) {
                    hcalc = hcalc + calc2;
                    hcalc = hcalc * 31;
                }
                if(!Time2_3.equals(Time1_3)) 
                    jcalc = jcalc + calc3;
                
                calc = gcalc + hcalc + jcalc;
            }
            
            //
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        String sql1 = "UPDATE appointments"
                + " SET statusCode = 5"
                + " WHERE appt_id = '" + apptId + "';";
        
        String sql2 = "UPDATE perfevel "
                + " SET radtime1 = '" + calc + "', role_id = 5"
                + " WHERE appt_jd = '" + apptId + "';";
        try {
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.execute(sql1);
            stmt.execute(sql2);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void updateAppointmentStatus1(String patID, String apptId) {

        String sql = "UPDATE appointments"
                + " SET statusCode = 5"
                + " WHERE appt_id = '" + apptId + "';";
        try {
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void downloadImage(Pair img, File selectedDirectory) {
        try {
            String mimeType = URLConnection.guessContentTypeFromStream(img.fis);
            System.out.print(mimeType);
            mimeType = mimeType.replace("image/", "");
            File outputFile = new File(selectedDirectory.getPath() + "/" + img.imgID + "." + mimeType);
            FileUtils.copyInputStreamToFile(img.fis, outputFile);
        } catch (IOException ex) {
            Logger.getLogger(ReferralDoctor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private class Pair {

        Image img;
        String imgID;
        InputStream fis;

        public Pair(Image img, String imgID) {
            this.img = img;
            this.imgID = imgID;
        }

        public Image getImg() {
            return img;
        }

        public void setImg(Image img) {
            this.img = img;
        }

        public String getImgID() {
            return imgID;
        }

        public void setImgID(String imgID) {
            this.imgID = imgID;
        }

    }

}
