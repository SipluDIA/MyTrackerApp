<?php
require_once 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $user_id = intval($_POST['user_id']);
    $walking = intval($_POST['walking']);
    $running = intval($_POST['running']);
    $swimming = intval($_POST['swimming']);
    
    // Get current month and year
    $month = date('Y-m');
    
    // Validate inputs
    if ($user_id <= 0) {
        echo json_encode([
            "success" => false,
            "message" => "Invalid user ID"
        ]);
        exit;
    }
    
    if ($walking < 0 || $running < 0 || $swimming < 0) {
        echo json_encode([
            "success" => false,
            "message" => "Goals cannot be negative"
        ]);
        exit;
    }
    
    // Check if goal already exists for this user and month
    $check_sql = "SELECT id FROM goals WHERE user_id = $user_id AND month = '$month'";
    $check_result = $conn->query($check_sql);
    
    if ($check_result->num_rows > 0) {
        // Update existing goal
        $update_sql = "UPDATE goals SET 
                       walking = $walking,
                       running = $running,
                       swimming = $swimming,
                       updated_at = CURRENT_TIMESTAMP
                       WHERE user_id = $user_id AND month = '$month'";
        
        if ($conn->query($update_sql)) {
            echo json_encode([
                "success" => true,
                "message" => "Goal updated successfully for " . date('F Y')
            ]);
        } else {
            echo json_encode([
                "success" => false,
                "message" => "Failed to update goal"
            ]);
        }
    } else {
        // Insert new goal
        $insert_sql = "INSERT INTO goals (user_id, walking, running, swimming, month) 
                       VALUES ($user_id, $walking, $running, $swimming, '$month')";
        
        if ($conn->query($insert_sql)) {
            echo json_encode([
                "success" => true,
                "message" => "Goal set successfully for " . date('F Y')
            ]);
        } else {
            echo json_encode([
                "success" => false,
                "message" => "Failed to set goal"
            ]);
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