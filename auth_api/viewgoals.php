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
    
    // Get all goals for the user, ordered by month descending (most recent first)
    $sql = "SELECT id, walking, running, swimming, month, created_at, updated_at 
            FROM goals 
            WHERE user_id = $user_id 
            ORDER BY month DESC";
    
    $result = $conn->query($sql);
    
    if ($result) {
        $goals = array();
        
        while ($row = $result->fetch_assoc()) {
            // Format month for better display
            $date = DateTime::createFromFormat('Y-m', $row['month']);
            $month_name = $date ? $date->format('F Y') : $row['month'];
            
            $goals[] = array(
                "id" => $row['id'],
                "walking" => $row['walking'],
                "running" => $row['running'],
                "swimming" => $row['swimming'],
                "month" => $row['month'],
                "month_name" => $month_name,
                "created_at" => $row['created_at'],
                "updated_at" => $row['updated_at']
            );
        }
        
        if (count($goals) > 0) {
            echo json_encode([
                "success" => true,
                "message" => "Goals retrieved successfully",
                "goals" => $goals,
                "total" => count($goals)
            ]);
        } else {
            echo json_encode([
                "success" => true,
                "message" => "No goals found",
                "goals" => [],
                "total" => 0
            ]);
        }
    } else {
        echo json_encode([
            "success" => false,
            "message" => "Failed to retrieve goals"
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