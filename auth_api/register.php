<?php
require_once 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $username = $conn->real_escape_string($_POST['username']);
    $email = $conn->real_escape_string($_POST['email']);
    $password = $_POST['password'];
    
    // Check if email already exists
    $check = $conn->query("SELECT id FROM users WHERE email='$email'");
    if ($check->num_rows > 0) {
        echo json_encode([
            "success" => false,
            "message" => "Email already registered"
        ]);
        exit;
    }
    
    // Check if username already exists
    $check = $conn->query("SELECT id FROM users WHERE username='$username'");
    if ($check->num_rows > 0) {
        echo json_encode([
            "success" => false,
            "message" => "Username already taken"
        ]);
        exit;
    }
    
    // Hash password
    $hashed_password = password_hash($password, PASSWORD_BCRYPT);
    
    // Insert user
    $sql = "INSERT INTO users (username, email, password) 
            VALUES ('$username', '$email', '$hashed_password')";
    
    if ($conn->query($sql)) {
        echo json_encode([
            "success" => true,
            "message" => "Registration successful"
        ]);
    } else {
        echo json_encode([
            "success" => false,
            "message" => "Registration failed"
        ]);
    }
}
$conn->close();
?>