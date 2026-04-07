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
    
    // Get last 7 days of statistics
    $weekly_stats = array();
    
    for ($i = 6; $i >= 0; $i--) {
        $date = date('Y-m-d', strtotime("-$i days"));
        $day_name = date('l', strtotime($date)); // Full day name (Monday, Tuesday, etc.)
        $day_short = date('D', strtotime($date)); // Short day name (Mon, Tue, etc.)
        
        // Get activities for this day
        $start_time = $date . ' 00:00:00';
        $end_time = $date . ' 23:59:59';
        
        $walking_sql = "SELECT COALESCE(COUNT(*), 0) as count, COALESCE(SUM(duration), 0) as duration
                        FROM activity 
                        WHERE user_id = $user_id 
                        AND activity_type = 'walking'
                        AND created_at BETWEEN '$start_time' AND '$end_time'";
        
        $running_sql = "SELECT COALESCE(COUNT(*), 0) as count, COALESCE(SUM(duration), 0) as duration
                        FROM activity 
                        WHERE user_id = $user_id 
                        AND activity_type = 'running'
                        AND created_at BETWEEN '$start_time' AND '$end_time'";
        
        $swimming_sql = "SELECT COALESCE(COUNT(*), 0) as count, COALESCE(SUM(duration), 0) as duration
                         FROM activity 
                         WHERE user_id = $user_id 
                         AND activity_type = 'swimming'
                         AND created_at BETWEEN '$start_time' AND '$end_time'";
        
        $walking_result = $conn->query($walking_sql);
        $running_result = $conn->query($running_sql);
        $swimming_result = $conn->query($swimming_sql);
        
        $walking_data = $walking_result->fetch_assoc();
        $running_data = $running_result->fetch_assoc();
        $swimming_data = $swimming_result->fetch_assoc();
        
        $walking_value = intval($walking_data['duration']);
        $running_value = intval($running_data['duration']);
        $swimming_value = intval($swimming_data['duration']);
        
        $daily_stat = array(
            "date" => $date,
            "day_name" => $day_name,
            "day_short" => $day_short,
            "walking" => $walking_value,
            "running" => $running_value,
            "swimming" => $swimming_value,
            "total" => $walking_value + $running_value + $swimming_value
        );
        
        array_push($weekly_stats, $daily_stat);
    }
    
    echo json_encode([
        "success" => true,
        "weekly_stats" => $weekly_stats
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Invalid request method"
    ]);
}
?>
