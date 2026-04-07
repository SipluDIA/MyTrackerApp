<?php
require_once 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $user_id = intval($_POST['user_id']);
    
    // Validate user ID
    if ($user_id <= 0) {
        echo json_encode([
            "success" => false,
            "message" => "Invalid user ID"
        ]);
        exit;
    }
    
    // Get all activities for the user, ordered by created_at descending
    $sql = "SELECT id, activity_type, start_time, end_time, duration, 
                   start_latitude, start_longitude, end_latitude, end_longitude, 
                   distance, created_at 
            FROM activity 
            WHERE user_id = $user_id 
            ORDER BY created_at DESC";
    
    $result = $conn->query($sql);
    
    if ($result) {
        $activities = array();
        
        while ($row = $result->fetch_assoc()) {
            // Format date for better display
            $date = date('F d, Y h:i A', strtotime($row['created_at']));
            
            $activities[] = array(
                "id" => $row['id'],
                "activity_type" => $row['activity_type'],
                "start_time" => $row['start_time'],
                "end_time" => $row['end_time'],
                "duration" => $row['duration'],
                "start_latitude" => $row['start_latitude'],
                "start_longitude" => $row['start_longitude'],
                "end_latitude" => $row['end_latitude'],
                "end_longitude" => $row['end_longitude'],
                "distance" => $row['distance'],
                "created_at" => $row['created_at'],
                "formatted_date" => $date
            );
        }
        
        if (count($activities) > 0) {
            echo json_encode([
                "success" => true,
                "message" => "Activities retrieved successfully",
                "activities" => $activities,
                "total" => count($activities)
            ]);
        } else {
            echo json_encode([
                "success" => true,
                "message" => "No activities found",
                "activities" => [],
                "total" => 0
            ]);
        }
    } else {
        echo json_encode([
            "success" => false,
            "message" => "Failed to retrieve activities"
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