<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

$host = "localhost";
$username = "root";
$password = "";
$database = "auth_system";

$conn = new mysqli($host, $username, $password, $database);

if ($conn->connect_error) {
    die(json_encode([
        "success" => false,
        "message" => "Database connection failed"
    ]));
}
?>