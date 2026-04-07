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
    
    // Get start and end of current week (Saturday to Friday)
    $today = new DateTime();
    $day_of_week = $today->format('w'); // 0 (Sunday) to 6 (Saturday)
    
    // Calculate days to subtract to get to Saturday
    $days_since_saturday = ($day_of_week + 1) % 7;
    
    $start_of_week = clone $today;
    $start_of_week->modify("-$days_since_saturday days");
    $start_of_week->setTime(0, 0, 0);
    
    $end_of_week = clone $start_of_week;
    $end_of_week->modify('+6 days');
    $end_of_week->setTime(23, 59, 59);
    
    $start_date = $start_of_week->format('Y-m-d H:i:s');
    $end_date = $end_of_week->format('Y-m-d H:i:s');
    
    // MET values for calorie calculation
    $met_values = [
        'Walking' => 3.5,
        'Running' => 9.8,
        'Swimming' => 6.0
    ];
    $weight_kg = 70; // Default weight
    
    // Get activities for the week grouped by date
    $sql = "SELECT DATE(created_at) as activity_date, activity_type, SUM(duration) as total_duration
            FROM activity 
            WHERE user_id = $user_id 
            AND created_at BETWEEN '$start_date' AND '$end_date'
            GROUP BY DATE(created_at), activity_type
            ORDER BY activity_date ASC";
    
    $result = $conn->query($sql);
    
    // Initialize week data structure
    $week_data = [];
    $current_date = clone $start_of_week;
    
    for ($i = 0; $i < 7; $i++) {
        $date_key = $current_date->format('Y-m-d');
        $day_name = $current_date->format('l'); // Full day name
        $day_short = strtolower(substr($day_name, 0, 2)); // First 2 letters lowercase
        
        $week_data[$date_key] = [
            'date' => $date_key,
            'day_name' => $day_name,
            'day_short' => $day_short,
            'calories' => 0
        ];
        
        $current_date->modify('+1 day');
    }
    
    // Calculate calories for each day
    if ($result && $result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $date = $row['activity_date'];
            $type = $row['activity_type'];
            $duration = intval($row['total_duration']);
            
            if (isset($week_data[$date]) && isset($met_values[$type])) {
                $met = $met_values[$type];
                $duration_hours = $duration / 3600;
                $calories = $met * $weight_kg * $duration_hours;
                $week_data[$date]['calories'] += $calories;
            }
        }
    }
    
    // Round calories to integers
    foreach ($week_data as &$day) {
        $day['calories'] = round($day['calories']);
    }
    
    // Convert to indexed array
    $weekly_calories = array_values($week_data);
    
    echo json_encode([
        "success" => true,
        "message" => "Weekly calories retrieved successfully",
        "weekly_calories" => $weekly_calories,
        "week_start" => $start_of_week->format('Y-m-d'),
        "week_end" => $end_of_week->format('Y-m-d')
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Invalid request method"
    ]);
}

$conn->close();
?>