<?php
require_once 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $email = $conn->real_escape_string($_POST['email']);
    $password = $_POST['password'];
    
    $sql = "SELECT id, username, password FROM users WHERE email='$email'";
    $result = $conn->query($sql);
    
    if ($result->num_rows > 0) {
        $user = $result->fetch_assoc();
        
        if (password_verify($password, $user['password'])) {
            echo json_encode([
                "success" => true,
                "message" => "Login successful",
                "username" => $user['username'],
                "user_id" => $user['id']
            ]);
        } else {
            echo json_encode([
                "success" => false,
                "message" => "Invalid password"
            ]);
        }
    } else {
        echo json_encode([
            "success" => false,
            "message" => "Email not found"
        ]);
    }
}
$conn->close();
?>