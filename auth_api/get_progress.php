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
    
    // Get current month
    $current_month = date('Y-m');
    
    // Get goals for current month
    $goals_sql = "SELECT walking, running, swimming 
                  FROM goals 
                  WHERE user_id = $user_id AND month = '$current_month'";
    $goals_result = $conn->query($goals_sql);
    
    if (!$goals_result || $goals_result->num_rows == 0) {
        echo json_encode([
            "success" => true,
            "message" => "No goals set for current month",
            "has_goals" => false
        ]);
        exit;
    }
    
    $goals = $goals_result->fetch_assoc();
    
    // Get activities for current month
    $start_of_month = date('Y-m-01 00:00:00');
    $end_of_month = date('Y-m-t 23:59:59');
    
    $activities_sql = "SELECT activity_type, SUM(distance) as total_distance, SUM(duration) as total_duration
                       FROM activity 
                       WHERE user_id = $user_id 
                       AND created_at BETWEEN '$start_of_month' AND '$end_of_month'
                       GROUP BY activity_type";
    
    $activities_result = $conn->query($activities_sql);
    
    // Initialize activity totals
    $walking_total = 0;
    $running_total = 0;
    $swimming_total = 0;
    $walking_duration = 0;
    $running_duration = 0;
    $swimming_duration = 0;
    
    if ($activities_result && $activities_result->num_rows > 0) {
        while ($row = $activities_result->fetch_assoc()) {
            $type = $row['activity_type'];
            $distance = floatval($row['total_distance']);
            $duration = intval($row['total_duration']);
            
            if ($type == 'Walking') {
                $walking_total = $distance;
                $walking_duration = $duration;
            } elseif ($type == 'Running') {
                $running_total = $distance;
                $running_duration = $duration;
            } elseif ($type == 'Swimming') {
                $swimming_total = $distance;
                $swimming_duration = $duration;
            }
        }
    }
    
    // Calculate percentages
    $walking_goal = intval($goals['walking']);
    $running_goal = intval($goals['running']);
    $swimming_goal = intval($goals['swimming']);
    
    $walking_percentage = $walking_goal > 0 ? min(100, ($walking_total / $walking_goal) * 100) : 0;
    $running_percentage = $running_goal > 0 ? min(100, ($running_total / $running_goal) * 100) : 0;
    $swimming_percentage = $swimming_goal > 0 ? min(100, ($swimming_total / $swimming_goal) * 100) : 0;
    
    // Calculate calories (MET × weight(kg) × duration(hours))
    $weight_kg = 70; // Default weight
    
    // MET values
    $walking_met = 3.5;
    $running_met = 9.8;
    $swimming_met = 6.0;
    
    $walking_calories = $walking_met * $weight_kg * ($walking_duration / 3600);
    $running_calories = $running_met * $weight_kg * ($running_duration / 3600);
    $swimming_calories = $swimming_met * $weight_kg * ($swimming_duration / 3600);
    
    echo json_encode([
        "success" => true,
        "message" => "Progress retrieved successfully",
        "has_goals" => true,
        "progress" => [
            "walking" => [
                "goal" => $walking_goal,
                "achieved" => round($walking_total, 2),
                "percentage" => round($walking_percentage, 1),
                "calories" => round($walking_calories, 0)
            ],
            "running" => [
                "goal" => $running_goal,
                "achieved" => round($running_total, 2),
                "percentage" => round($running_percentage, 1),
                "calories" => round($running_calories, 0)
            ],
            "swimming" => [
                "goal" => $swimming_goal,
                "achieved" => round($swimming_total, 2),
                "percentage" => round($swimming_percentage, 1),
                "calories" => round($swimming_calories, 0)
            ]
        ]
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Invalid request method"
    ]);
}

$conn->close();
?>