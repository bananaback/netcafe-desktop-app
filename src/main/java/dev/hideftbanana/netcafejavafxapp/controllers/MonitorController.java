package dev.hideftbanana.netcafejavafxapp.controllers;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import dev.hideftbanana.netcafejavafxapp.models.Room;
import dev.hideftbanana.netcafejavafxapp.models.request.ComputerIdsRequest;
import dev.hideftbanana.netcafejavafxapp.models.responses.ComputerResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.OrderItemDTO;
import dev.hideftbanana.netcafejavafxapp.models.responses.OrderResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.OrderStatusEnum;
import dev.hideftbanana.netcafejavafxapp.models.responses.ProductResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.RoomResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.UserSessionResponse;
import dev.hideftbanana.netcafejavafxapp.services.cacheservices.ImageCache;
import dev.hideftbanana.netcafejavafxapp.services.computerservices.ComputerService;
import dev.hideftbanana.netcafejavafxapp.services.orderitemservices.OrderItemService;
import dev.hideftbanana.netcafejavafxapp.services.orderservices.OrderService;
import dev.hideftbanana.netcafejavafxapp.services.productservices.ProductService;
import dev.hideftbanana.netcafejavafxapp.services.roomservices.RoomService;
import dev.hideftbanana.netcafejavafxapp.services.userserssionservices.UserSessionService;
import dev.hideftbanana.netcafejavafxapp.services.userservices.UserService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class MonitorController extends BaseController implements Initializable {

        private ComputerService computerService;
        private RoomService roomService;
        private ImageCache imageCache;
        private UserSessionService userSessionService;
        private UserService userService;
        private OrderService orderService;
        private OrderItemService orderItemService;
        private ProductService productService;

        @FXML
        private HBox roomHBox;
        @FXML
        private FlowPane computersFlowPane;

        @FXML
        private Label computerIdLabel;
        @FXML
        private Label computerConfigurationsLabel;
        @FXML
        private Label computerPricePerHourLabel;

        @FXML
        private Label usernameLabel;
        @FXML
        private Label usedLabel;
        @FXML
        private Label remainLabel;
        @FXML
        private Label checkinLabel;

        @FXML
        private ScrollPane ordersScrollPane;
        @FXML
        private VBox ordersVBox;
        @FXML
        private VBox messagesVBox;
        @FXML
        private Button ordersButton;
        @FXML
        private Button communicateButton;
        private Long currentUserId;

        @FXML
        private void checkoutUser() {
                // Construct the API URL
                String apiUrl = "http://localhost:8080/api/auth/logout/" + currentUserId;

                // Create a DELETE request
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(apiUrl))
                                .DELETE()
                                .build();
                HttpClient httpClient = HttpClient.newHttpClient();
                // Send the DELETE request asynchronously
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                .thenAccept(response -> {
                                        // Check the response status code
                                        Platform.runLater(() -> {
                                                if (response.statusCode() == 200) {
                                                        // Successful logout
                                                        showAlert(Alert.AlertType.INFORMATION,
                                                                        "Checkout User Successful",
                                                                        "You have been checkout user successfully.");
                                                } else {
                                                        // Failed to logout
                                                        showAlert(Alert.AlertType.ERROR, "Checkout User Failed",
                                                                        "Failed to Checkout User. Please try again.");
                                                }
                                        });
                                })
                                .exceptionally(ex -> {
                                        // Exception occurred
                                        Platform.runLater(() -> {
                                                // Show an error message to the user
                                                showAlert(Alert.AlertType.ERROR, "Error",
                                                                "An error occurred while trying to checkout user: "
                                                                                + ex.getMessage());
                                        });
                                        return null;
                                });
        }

        private void showAlert(Alert.AlertType type, String title, String content) {
                Alert alert = new Alert(type);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(content);
                alert.showAndWait();
        }

        @FXML
        private void showOrders() {
                ordersScrollPane.setVisible(true);
                messagesVBox.setVisible(false);
                ordersButton.setStyle("-fx-background-color:  #4580C6;");
                communicateButton.setStyle("-fx-background-color: transparent;");
        }

        @FXML
        private void showMessages() {
                ordersScrollPane.setVisible(false);
                messagesVBox.setVisible(true);
                ordersButton.setStyle("-fx-background-color:  transparent;");
                communicateButton.setStyle("-fx-background-color: #4580C6;");
        }

        public MonitorController() {
                this.computerService = new ComputerService();
                this.roomService = new RoomService();
                this.userSessionService = new UserSessionService();
                this.userService = new UserService();
                this.orderService = new OrderService();
                this.orderItemService = new OrderItemService();
                this.productService = new ProductService();
        }

        @Override
        public void initialize(URL arg0, ResourceBundle arg1) {
                renderButtons();
                ordersScrollPane.setVisible(true);
                messagesVBox.setVisible(false);
        }

        private void renderButtons() {
                roomService.getAllRooms()
                                .thenAcceptAsync(roomsResponse -> {
                                        ObservableList<Room> newRoomList = FXCollections.observableArrayList();
                                        for (RoomResponse r : roomsResponse.getRooms()) {
                                                Room room = new Room();
                                                room.setId(r.getId());
                                                room.setName(r.getName());
                                                newRoomList.add(room);
                                        }

                                        Platform.runLater(() -> {
                                                roomHBox.getChildren().clear(); // Clear existing buttons
                                                for (Room room : newRoomList) {
                                                        Button roomButton = new Button(room.getName());
                                                        roomButton.setStyle(
                                                                        "-fx-background-color: transparent; -fx-text-fill: white;");
                                                        roomButton.setFont(Font.font("System Bold", 18));
                                                        roomButton.setOnAction(event -> {
                                                                computersFlowPane.getChildren().clear();
                                                                // Handle button click, you have access to room.getId()
                                                                System.out.println("Room ID: " + room.getId());

                                                                // Handle button click, fetch computers in the room
                                                                computerService.getComputersByRoom(room.getId())
                                                                                .thenAcceptAsync(computers -> {
                                                                                        Platform.runLater(() -> {
                                                                                                computersFlowPane
                                                                                                                .getChildren()
                                                                                                                .clear(); // Clear
                                                                                                                          // existing
                                                                                                                          // computers
                                                                                                List<Long> computerIds = computers
                                                                                                                .stream()
                                                                                                                .map(ComputerResponse::getId)
                                                                                                                .collect(Collectors
                                                                                                                                .toList());

                                                                                                // Fetch the latest user
                                                                                                // sessions for the
                                                                                                // computers in this
                                                                                                // room
                                                                                                userSessionService
                                                                                                                .getLatestUserSessionsByComputerIds(
                                                                                                                                new ComputerIdsRequest(
                                                                                                                                                computerIds))
                                                                                                                .thenAcceptAsync(
                                                                                                                                userSessions -> {
                                                                                                                                        for (ComputerResponse computer : computers) {
                                                                                                                                                ImageView imageView = new ImageView();
                                                                                                                                                String imageName = "cpu_light.jpg"; // Default
                                                                                                                                                                                    // to
                                                                                                                                                                                    // dark
                                                                                                                                                                                    // image
                                                                                                                                                for (UserSessionResponse session : userSessions) {
                                                                                                                                                        if (session.getComputerId() == computer
                                                                                                                                                                        .getId()) {
                                                                                                                                                                if (session.getStatus()
                                                                                                                                                                                .toString()
                                                                                                                                                                                .equals("ONGOING")) {
                                                                                                                                                                        imageName = "cpu_dark.jpg";
                                                                                                                                                                        break; // No
                                                                                                                                                                               // need
                                                                                                                                                                               // to
                                                                                                                                                                               // check
                                                                                                                                                                               // other
                                                                                                                                                                               // sessions
                                                                                                                                                                               // for
                                                                                                                                                                               // this
                                                                                                                                                                               // computer
                                                                                                                                                                }
                                                                                                                                                        }
                                                                                                                                                }
                                                                                                                                                byte[] imageData = imageCache
                                                                                                                                                                .get(imageName);
                                                                                                                                                Image image = new Image(
                                                                                                                                                                new ByteArrayInputStream(
                                                                                                                                                                                imageData));
                                                                                                                                                imageView.setImage(
                                                                                                                                                                image);
                                                                                                                                                imageView.setFitWidth(
                                                                                                                                                                120);
                                                                                                                                                imageView.setFitHeight(
                                                                                                                                                                100);
                                                                                                                                                imageView.setPreserveRatio(
                                                                                                                                                                true);

                                                                                                                                                imageView.setOnMouseClicked(
                                                                                                                                                                mouseEvent -> {
                                                                                                                                                                        // Handle
                                                                                                                                                                        // computer
                                                                                                                                                                        // click,
                                                                                                                                                                        // you
                                                                                                                                                                        // have
                                                                                                                                                                        // access
                                                                                                                                                                        // to
                                                                                                                                                                        // computer.getId()
                                                                                                                                                                        System.out.println(
                                                                                                                                                                                        "Computer ID: " + computer
                                                                                                                                                                                                        .getId());

                                                                                                                                                                        // Fetch
                                                                                                                                                                        // computer
                                                                                                                                                                        // details
                                                                                                                                                                        // based
                                                                                                                                                                        // on
                                                                                                                                                                        // computer
                                                                                                                                                                        // ID
                                                                                                                                                                        // and
                                                                                                                                                                        // update
                                                                                                                                                                        // labels
                                                                                                                                                                        computerService.getComputerById(
                                                                                                                                                                                        computer.getId())
                                                                                                                                                                                        .thenAcceptAsync(
                                                                                                                                                                                                        computerDetails -> {
                                                                                                                                                                                                                Platform.runLater(
                                                                                                                                                                                                                                () -> {
                                                                                                                                                                                                                                        // Update
                                                                                                                                                                                                                                        // labels
                                                                                                                                                                                                                                        // with
                                                                                                                                                                                                                                        // computer
                                                                                                                                                                                                                                        // information
                                                                                                                                                                                                                                        computerIdLabel.setText(
                                                                                                                                                                                                                                                        "ID: "
                                                                                                                                                                                                                                                                        + computerDetails
                                                                                                                                                                                                                                                                                        .getId());
                                                                                                                                                                                                                                        computerConfigurationsLabel
                                                                                                                                                                                                                                                        .setText("Configurations: "
                                                                                                                                                                                                                                                                        + computerDetails
                                                                                                                                                                                                                                                                                        .getConfiguration());
                                                                                                                                                                                                                                        computerPricePerHourLabel
                                                                                                                                                                                                                                                        .setText("Price per Hour: "
                                                                                                                                                                                                                                                                        + computerDetails
                                                                                                                                                                                                                                                                                        .getPricePerHour());
                                                                                                                                                                                                                                });
                                                                                                                                                                                                        })
                                                                                                                                                                                        .exceptionally(throwable -> {
                                                                                                                                                                                                throwable.printStackTrace();
                                                                                                                                                                                                // Handle
                                                                                                                                                                                                // exceptions
                                                                                                                                                                                                // if
                                                                                                                                                                                                // needed
                                                                                                                                                                                                return null;
                                                                                                                                                                                        });

                                                                                                                                                                        // Fetch
                                                                                                                                                                        // user
                                                                                                                                                                        // session
                                                                                                                                                                        // details
                                                                                                                                                                        // for
                                                                                                                                                                        // the
                                                                                                                                                                        // clicked
                                                                                                                                                                        // computer
                                                                                                                                                                        userSessionService
                                                                                                                                                                                        .getLatestUserSessionsByComputerIds(
                                                                                                                                                                                                        new ComputerIdsRequest(
                                                                                                                                                                                                                        computerIds))
                                                                                                                                                                                        .thenAcceptAsync(
                                                                                                                                                                                                        userSessions2 -> {
                                                                                                                                                                                                                boolean foundSession = false;
                                                                                                                                                                                                                for (UserSessionResponse session : userSessions2) {
                                                                                                                                                                                                                        if (session
                                                                                                                                                                                                                                        .getComputerId() == computer
                                                                                                                                                                                                                                                        .getId()) {
                                                                                                                                                                                                                                if (session.getStatus()
                                                                                                                                                                                                                                                .toString()
                                                                                                                                                                                                                                                .equals("ONGOING")) {
                                                                                                                                                                                                                                        foundSession = true;
                                                                                                                                                                                                                                        Platform.runLater(
                                                                                                                                                                                                                                                        () -> {
                                                                                                                                                                                                                                                                // Update
                                                                                                                                                                                                                                                                // labels
                                                                                                                                                                                                                                                                // with
                                                                                                                                                                                                                                                                // user
                                                                                                                                                                                                                                                                // session
                                                                                                                                                                                                                                                                // information
                                                                                                                                                                                                                                                                usernameLabel.setText(
                                                                                                                                                                                                                                                                                "Username: ");
                                                                                                                                                                                                                                                                checkinLabel.setText(
                                                                                                                                                                                                                                                                                "Check-in: "
                                                                                                                                                                                                                                                                                                + session
                                                                                                                                                                                                                                                                                                                .getCheckinAt());
                                                                                                                                                                                                                                                                // Calculate
                                                                                                                                                                                                                                                                // used
                                                                                                                                                                                                                                                                // time
                                                                                                                                                                                                                                                                // and
                                                                                                                                                                                                                                                                // remaining
                                                                                                                                                                                                                                                                // time
                                                                                                                                                                                                                                                                // here
                                                                                                                                                                                                                                                                // based
                                                                                                                                                                                                                                                                // on
                                                                                                                                                                                                                                                                // user
                                                                                                                                                                                                                                                                // session
                                                                                                                                                                                                                                                                // information
                                                                                                                                                                                                                                                                // Calculate
                                                                                                                                                                                                                                                                // used
                                                                                                                                                                                                                                                                // time
                                                                                                                                                                                                                                                                LocalDateTime now = LocalDateTime
                                                                                                                                                                                                                                                                                .now();
                                                                                                                                                                                                                                                                Duration usedDuration = Duration
                                                                                                                                                                                                                                                                                .between(session
                                                                                                                                                                                                                                                                                                .getCheckinAt(),
                                                                                                                                                                                                                                                                                                now);
                                                                                                                                                                                                                                                                long hours = usedDuration
                                                                                                                                                                                                                                                                                .toHours();
                                                                                                                                                                                                                                                                long minutes = usedDuration
                                                                                                                                                                                                                                                                                .minusHours(
                                                                                                                                                                                                                                                                                                hours)
                                                                                                                                                                                                                                                                                .toMinutes();
                                                                                                                                                                                                                                                                String usedTimeString = String
                                                                                                                                                                                                                                                                                .format("%d hours and %d minutes",
                                                                                                                                                                                                                                                                                                hours,
                                                                                                                                                                                                                                                                                                minutes);
                                                                                                                                                                                                                                                                usedLabel.setText(
                                                                                                                                                                                                                                                                                "Used: " + usedTimeString); // Update
                                                                                                                                                                                                                                                                                                            // with
                                                                                                                                                                                                                                                                                                            // calculated
                                                                                                                                                                                                                                                                                                            // used
                                                                                                                                                                                                                                                                                                            // time
                                                                                                                                                                                                                                                                // Get
                                                                                                                                                                                                                                                                // user
                                                                                                                                                                                                                                                                // info
                                                                                                                                                                                                                                                                // from
                                                                                                                                                                                                                                                                // UserService
                                                                                                                                                                                                                                                                // using
                                                                                                                                                                                                                                                                // the
                                                                                                                                                                                                                                                                // user
                                                                                                                                                                                                                                                                // ID
                                                                                                                                                                                                                                                                // from
                                                                                                                                                                                                                                                                // the
                                                                                                                                                                                                                                                                // session
                                                                                                                                                                                                                                                                userService
                                                                                                                                                                                                                                                                                .getUserInfoById(
                                                                                                                                                                                                                                                                                                session.getUserId())
                                                                                                                                                                                                                                                                                .thenAcceptAsync(
                                                                                                                                                                                                                                                                                                userInfo -> {

                                                                                                                                                                                                                                                                                                        // Calculate
                                                                                                                                                                                                                                                                                                        // remaining
                                                                                                                                                                                                                                                                                                        // time
                                                                                                                                                                                                                                                                                                        // based
                                                                                                                                                                                                                                                                                                        // on
                                                                                                                                                                                                                                                                                                        // user
                                                                                                                                                                                                                                                                                                        // balance
                                                                                                                                                                                                                                                                                                        // and
                                                                                                                                                                                                                                                                                                        // price
                                                                                                                                                                                                                                                                                                        // per
                                                                                                                                                                                                                                                                                                        // hour
                                                                                                                                                                                                                                                                                                        double pricePerHour = computer
                                                                                                                                                                                                                                                                                                                        .getPricePerHour();
                                                                                                                                                                                                                                                                                                        // price
                                                                                                                                                                                                                                                                                                        // per
                                                                                                                                                                                                                                                                                                        // hour
                                                                                                                                                                                                                                                                                                        double remainingBalance = userInfo
                                                                                                                                                                                                                                                                                                                        .getBalance(); // User
                                                                                                                                                                                                                                                                                                                                       // balance
                                                                                                                                                                                                                                                                                                        long remainingHours = (long) (remainingBalance
                                                                                                                                                                                                                                                                                                                        / pricePerHour);
                                                                                                                                                                                                                                                                                                        long remainingMinutes = (long) ((remainingBalance
                                                                                                                                                                                                                                                                                                                        - remainingHours
                                                                                                                                                                                                                                                                                                                                        * pricePerHour)
                                                                                                                                                                                                                                                                                                                        * 60);
                                                                                                                                                                                                                                                                                                        String remainTimeString = String
                                                                                                                                                                                                                                                                                                                        .format("%d hours and %d minutes",
                                                                                                                                                                                                                                                                                                                                        remainingHours,
                                                                                                                                                                                                                                                                                                                                        remainingMinutes);
                                                                                                                                                                                                                                                                                                        Platform.runLater(
                                                                                                                                                                                                                                                                                                                        () -> {
                                                                                                                                                                                                                                                                                                                                usernameLabel
                                                                                                                                                                                                                                                                                                                                                .setText(
                                                                                                                                                                                                                                                                                                                                                                "Username: "
                                                                                                                                                                                                                                                                                                                                                                                + userInfo
                                                                                                                                                                                                                                                                                                                                                                                                .getUsername());
                                                                                                                                                                                                                                                                                                                                remainLabel
                                                                                                                                                                                                                                                                                                                                                .setText(
                                                                                                                                                                                                                                                                                                                                                                "Remain: "
                                                                                                                                                                                                                                                                                                                                                                                + remainTimeString); // Update
                                                                                                                                                                                                                                                                                                                                                                                                     // with
                                                                                                                                                                                                                                                                                                                                                                                                     // calculated
                                                                                                                                                                                                                                                                                                                                                                                                     // remaining
                                                                                                                                                                                                                                                                                                                                                                                                     // time
                                                                                                                                                                                                                                                                                                                                loadOrders(userInfo
                                                                                                                                                                                                                                                                                                                                                .getId());
                                                                                                                                                                                                                                                                                                                                currentUserId = userInfo
                                                                                                                                                                                                                                                                                                                                                .getId();
                                                                                                                                                                                                                                                                                                                        });

                                                                                                                                                                                                                                                                                                })
                                                                                                                                                                                                                                                                                .exceptionally(
                                                                                                                                                                                                                                                                                                throwable -> {
                                                                                                                                                                                                                                                                                                        throwable
                                                                                                                                                                                                                                                                                                                        .printStackTrace();
                                                                                                                                                                                                                                                                                                        // Handle
                                                                                                                                                                                                                                                                                                        // exceptions
                                                                                                                                                                                                                                                                                                        // if
                                                                                                                                                                                                                                                                                                        // needed
                                                                                                                                                                                                                                                                                                        return null;
                                                                                                                                                                                                                                                                                                });
                                                                                                                                                                                                                                                        });
                                                                                                                                                                                                                                        break;
                                                                                                                                                                                                                                }
                                                                                                                                                                                                                        }
                                                                                                                                                                                                                }
                                                                                                                                                                                                                if (!foundSession) {
                                                                                                                                                                                                                        Platform.runLater(
                                                                                                                                                                                                                                        () -> {
                                                                                                                                                                                                                                                // Reset
                                                                                                                                                                                                                                                // labels
                                                                                                                                                                                                                                                // if
                                                                                                                                                                                                                                                // no
                                                                                                                                                                                                                                                // user
                                                                                                                                                                                                                                                // session
                                                                                                                                                                                                                                                // is
                                                                                                                                                                                                                                                // found
                                                                                                                                                                                                                                                usernameLabel
                                                                                                                                                                                                                                                                .setText("Username: ");
                                                                                                                                                                                                                                                checkinLabel
                                                                                                                                                                                                                                                                .setText("Check-in: ");
                                                                                                                                                                                                                                                usedLabel.setText(
                                                                                                                                                                                                                                                                "Used: ");
                                                                                                                                                                                                                                                remainLabel.setText(
                                                                                                                                                                                                                                                                "Remain: ");
                                                                                                                                                                                                                                        });
                                                                                                                                                                                                                }
                                                                                                                                                                                                        })
                                                                                                                                                                                        .exceptionally(throwable -> {
                                                                                                                                                                                                throwable.printStackTrace();
                                                                                                                                                                                                // Handle
                                                                                                                                                                                                // exceptions
                                                                                                                                                                                                // if
                                                                                                                                                                                                // needed
                                                                                                                                                                                                return null;
                                                                                                                                                                                        });

                                                                                                                                                                });

                                                                                                                                                Platform.runLater(
                                                                                                                                                                () -> {
                                                                                                                                                                        computersFlowPane
                                                                                                                                                                                        .getChildren()
                                                                                                                                                                                        .add(imageView);
                                                                                                                                                                });
                                                                                                                                        }
                                                                                                                                })
                                                                                                                .exceptionally(throwable -> {
                                                                                                                        throwable.printStackTrace();
                                                                                                                        // Handle
                                                                                                                        // exceptions
                                                                                                                        // if
                                                                                                                        // needed
                                                                                                                        return null;
                                                                                                                });
                                                                                        });
                                                                                })
                                                                                .exceptionally(throwable -> {
                                                                                        throwable.printStackTrace();
                                                                                        // Handle exceptions if needed
                                                                                        return null;
                                                                                });
                                                        });
                                                        roomHBox.getChildren().add(roomButton);
                                                }
                                        });
                                })
                                .exceptionally(throwable -> {
                                        throwable.printStackTrace();
                                        // Handle exceptions if needed
                                        return null;
                                });
        }

        private void loadOrders(Long userId) {
                System.out.println("Here");
                orderService.getOrdersByUserId(userId)
                                .thenAcceptAsync(orders -> displayOrders(orders.getOrders()))
                                .exceptionally(ex -> {
                                        ex.printStackTrace();
                                        System.out.println("Failed to load orders: " + ex.getMessage());
                                        // Handle the exception here, e.g., show an error message to the user
                                        return null;
                                });
        }

        private void displayOrders(List<OrderResponse> orders) {
                Platform.runLater(() -> {
                        ordersVBox.getChildren().clear(); // Clear existing orders

                        for (OrderResponse order : orders) {
                                // Create VBox for each order
                                VBox orderVBox = new VBox();
                                orderVBox.setPadding(new Insets(10));
                                orderVBox.setSpacing(10);
                                orderVBox.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

                                // Create label for order information
                                Label orderInfoLabel = new Label(
                                                "Order ID: " + order.getId() + ", Status: " + order.getOrderStatus());
                                orderInfoLabel.setStyle("-fx-text-fill: #4CAF50;"); // Green color for the label text

                                orderVBox.getChildren().add(orderInfoLabel);

                                // Call OrderItemService to get order items for this order
                                orderItemService.getAllOrderItemsOfOrder(order.getId())
                                                .thenAcceptAsync(orderItems -> {
                                                        // Create VBox for order items
                                                        VBox orderItemsVBox = new VBox();
                                                        orderItemsVBox.setSpacing(5);

                                                        // Initialize total price for the order
                                                        float totalPrice = 0;

                                                        // Iterate through order items
                                                        for (OrderItemDTO orderItem : orderItems.getOrderItems()) {
                                                                float orderItemPrice = orderItem.getQuantity()
                                                                                * orderItem.getSinglePrice();
                                                                totalPrice += orderItemPrice;
                                                                // Call ProductService to get product information
                                                                productService.getAllProducts()
                                                                                .thenAcceptAsync(productsResponse -> {
                                                                                        // Find the product with the
                                                                                        // corresponding product ID
                                                                                        ProductResponse product = productsResponse
                                                                                                        .getProducts()
                                                                                                        .stream()
                                                                                                        .filter(p -> p.getId()
                                                                                                                        .equals(orderItem
                                                                                                                                        .getProductId()))
                                                                                                        .findFirst()
                                                                                                        .orElse(null);

                                                                                        if (product != null) {
                                                                                                // Calculate price for
                                                                                                // the order item

                                                                                                // Add price information
                                                                                                // to the label
                                                                                                Label orderItemLabel = new Label(
                                                                                                                "Product: " + product
                                                                                                                                .getName()
                                                                                                                                + ", Quantity: "
                                                                                                                                + orderItem.getQuantity()
                                                                                                                                + ", Price: $"
                                                                                                                                + orderItemPrice);
                                                                                                orderItemLabel.setStyle(
                                                                                                                "-fx-text-fill: #2196F3;"); // Blue
                                                                                                                                            // color
                                                                                                                                            // for
                                                                                                                                            // the
                                                                                                                                            // label
                                                                                                                                            // text

                                                                                                // Add order item price
                                                                                                // to the total price
                                                                                                // for the order

                                                                                                Platform.runLater(
                                                                                                                () -> {
                                                                                                                        // Add
                                                                                                                        // label
                                                                                                                        // to
                                                                                                                        // the
                                                                                                                        // VBox
                                                                                                                        orderItemsVBox.getChildren()
                                                                                                                                        .add(orderItemLabel);
                                                                                                                });
                                                                                        }
                                                                                }).exceptionally(ex -> {
                                                                                        ex.printStackTrace();
                                                                                        System.out.println(
                                                                                                        "Failed to load product: "
                                                                                                                        + ex.getMessage());
                                                                                        return null;
                                                                                });
                                                        }

                                                        // Display total price for the order
                                                        Label totalPriceLabel = new Label(
                                                                        "Total Price: $" + totalPrice);
                                                        totalPriceLabel.setStyle("-fx-text-fill: #FF5722;"); // Orange
                                                                                                             // color
                                                                                                             // for the
                                                                                                             // label
                                                                                                             // text

                                                        orderItemsVBox.getChildren().add(totalPriceLabel);

                                                        Platform.runLater(() -> {
                                                                // Add order items VBox to order VBox
                                                                orderVBox.getChildren().add(orderItemsVBox);
                                                        });
                                                })
                                                .exceptionally(ex -> {
                                                        ex.printStackTrace();
                                                        System.out.println("Failed to load order items: "
                                                                        + ex.getMessage());
                                                        return null;
                                                });

                                // Create button for changing order status
                                Button statusButton = new Button("Change Status");
                                statusButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;"); // Orange
                                                                                                               // background
                                                                                                               // with
                                                                                                               // white
                                                                                                               // text

                                statusButton.setOnAction(e -> changeOrderStatus(order));

                                // Add button to order VBox
                                orderVBox.getChildren().add(statusButton);

                                // Add order VBox to orders VBox
                                ordersVBox.getChildren().add(orderVBox);
                        }
                });
        }

        private void changeOrderStatus(OrderResponse orderResponse) {
                OrderStatusEnum currentStatus = orderResponse.getOrderStatus();
                OrderStatusEnum newStatus;

                // Determine the new status based on the current status
                switch (currentStatus) {
                        case PENDING:
                                newStatus = OrderStatusEnum.PAID;
                                break;
                        case PAID:
                                newStatus = OrderStatusEnum.FINISHED;
                                break;
                        default:
                                // Return if the current status is not PENDING or PAID
                                return;
                }

                // Call the updateOrderStatus method in the OrderService
                orderService.updateOrderStatus(orderResponse.getId(), newStatus)
                                .thenAcceptAsync(message -> {
                                        // If the update is successful, reload the orders
                                        loadOrders(currentUserId);
                                })
                                .exceptionally(ex -> {
                                        // If there's an exception, inform the user about the failure
                                        Platform.runLater(() -> {
                                                // Show an alert or message to inform the user about the failure
                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                alert.setTitle("Error");
                                                alert.setHeaderText("Failed to update order status");
                                                alert.setContentText(ex.getMessage());
                                                alert.showAndWait();
                                        });
                                        return null;
                                });
        }

        public void setImageCache(ImageCache imageCache) {
                this.imageCache = imageCache;
        }
}
