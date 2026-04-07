<?php
require_once 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $user_id = intval($_POST['user_id']);
    $activity_type = $conn->real_escape_string($_POST['activity_type']);
    $start_time = $conn->real_escape_string($_POST['start_time']);
    $end_time = $conn->real_escape_string($_POST['end_time']);
    $duration = intval($_POST['duration']);
    $start_latitude = floatval($_POST['start_latitude']);
    $start_longitude = floatval($_POST['start_longitude']);
    $end_latitude = floatval($_POST['end_latitude']);
    $end_longitude = floatval($_POST['end_longitude']);
    $distance = floatval($_POST['distance']);
    
    // Validate inputs
    if ($user_id <= 0) {
        echo json_encode([
            "success" => false,
            "message" => "Invalid user ID"
        ]);
        exit;
    }
    
    if (empty($activity_type)) {
        echo json_encode([
            "success" => false,
            "message" => "Activity type is required"
        ]);
        exit;
    }
    
    if ($duration <= 0) {
        echo json_encode([
            "success" => false,
            "message" => "Invalid duration"
        ]);
        exit;
    }
    
    // Insert activity
    $sql = "INSERT INTO activity (user_id, activity_type, start_time, end_time, duration, 
            start_latitude, start_longitude, end_latitude, end_longitude, distance) 
            VALUES ($user_id, '$activity_type', '$start_time', '$end_time', $duration, 
            $start_latitude, $start_longitude, $end_latitude, $end_longitude, $distance)";
    
    if ($conn->query($sql)) {
        $activity_id = $conn->insert_id;
        echo json_encode([
            "success" => true,
            "message" => "Activity saved successfully",
            "activity_id" => $activity_id
        ]);
    } else {
        echo json_encode([
            "success" => false,
            "message" => "Failed to save activity: " . $conn->error
        ]);
    }
} else {
    echo json_encode([
        "success" => false,
        "message" => "Invalid request method"
    ]);
}

$conn->close();
?>