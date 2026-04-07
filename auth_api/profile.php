<?php
require_once 'db_config.php';

// Handle both GET and POST requests
$request_method = $_SERVER['REQUEST_METHOD'];

if ($request_method == 'POST') {
    // Check if it's a save request or get request
    $action = isset($_POST['action']) ? $_POST['action'] : 'save';
    
    if ($action == 'get') {
        // GET PROFILE DATA
        $user_id = intval($_POST['user_id']);
        
        if ($user_id <= 0) {
            echo json_encode([
                "success" => false,
                "message" => "Invalid user ID"
            ]);
            exit;
        }
        
        $sql = "SELECT age, gender, weight, height FROM profile WHERE user_id = $user_id";
        $result = $conn->query($sql);
        
        if ($result && $result->num_rows > 0) {
            $profile = $result->fetch_assoc();
            echo json_encode([
                "success" => true,
                "message" => "Profile found",
                "profile" => $profile,
                "exists" => true
            ]);
        } else {
            echo json_encode([
                "success" => true,
                "message" => "No profile found",
                "profile" => null,
                "exists" => false
            ]);
        }
    } 
    else if ($action == 'save') {
        // SAVE/UPDATE PROFILE DATA
        $user_id = intval($_POST['user_id']);
        $age = intval($_POST['age']);
        $gender = $conn->real_escape_string($_POST['gender']);
        $weight = floatval($_POST['weight']);
        $height = floatval($_POST['height']);
        
        // Validate inputs
        if ($user_id <= 0) {
            echo json_encode([
                "success" => false,
                "message" => "Invalid user ID"
            ]);
            exit;
        }
        
        if ($age <= 0 || $age > 150) {
            echo json_encode([
                "success" => false,
                "message" => "Age must be between 1 and 150"
            ]);
            exit;
        }
        
        if ($weight <= 0 || $weight > 500) {
            echo json_encode([
                "success" => false,
                "message" => "Weight must be between 1 and 500 kg"
            ]);
            exit;
        }
        
        if ($height <= 0 || $height > 300) {
            echo json_encode([
                "success" => false,
                "message" => "Height must be between 1 and 300 cm"
            ]);
            exit;
        }
        
        if (empty($gender)) {
            echo json_encode([
                "success" => false,
                "message" => "Gender is required"
            ]);
            exit;
        }
        
        // Check if profile exists
        $check_sql = "SELECT id FROM profile WHERE user_id = $user_id";
        $check_result = $conn->query($check_sql);
        
        if ($check_result->num_rows > 0) {
            // UPDATE existing profile
            $update_sql = "UPDATE profile SET 
                          age = $age,
                          gender = '$gender',
                          weight = $weight,
                          height = $height,
                          updated_at = CURRENT_TIMESTAMP
                          WHERE user_id = $user_id";
            
            if ($conn->query($update_sql)) {
                echo json_encode([
                    "success" => true,
                    "message" => "Profile updated successfully"
                ]);
            } else {
                echo json_encode([
                    "success" => false,
                    "message" => "Failed to update profile"
                ]);
            }
        } else {
            // INSERT new profile
            $insert_sql = "INSERT INTO profile (user_id, age, gender, weight, height) 
                          VALUES ($user_id, $age, '$gender', $weight, $height)";
            
            if ($conn->query($insert_sql)) {
                echo json_encode([
                    "success" => true,
                    "message" => "Profile created successfully"
                ]);
            } else {
                echo json_encode([
                    "success" => false,
                    "message" => "Failed to create profile"
                ]);
            }
        }
    }
} else {
    echo json_encode([
        "success" => false,
        "message" => "Invalid request method"
    ]);
}

$conn->close();
?>