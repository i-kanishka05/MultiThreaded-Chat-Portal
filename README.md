# Multi-Threaded Socket Chat Portal

A real-time chat application developed using Java Socket Programming and Multithreading.

## Project Overview

This project implements a client-server based chat portal where multiple users can connect to a central server and communicate with each other simultaneously.

The server creates a separate thread for every connected client, allowing multiple users to communicate at the same time without blocking other connections.

## Technologies Used

- Java
- Socket Programming
- Multithreading
- TCP/IP
- ConcurrentHashMap
- File Handling
- Exception Handling

## Features

- Multi-client communication
- Multi-threaded server
- Username-based login
- Real-time group messaging
- Private messaging
- Online users list
- Join and leave notifications
- Message timestamps
- Server activity logging
- Graceful client disconnection
- Command-based communication
- Thread-safe client management

## Available Commands

```text
/help