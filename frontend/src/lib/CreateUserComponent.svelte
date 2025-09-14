<script>
  import { apiUrl, ENDPOINTS } from "./config.js";

  let username = $state("");
  let email = $state("");
  let loading = $state(false);
  let error = $state("");
  let createdUser = $state(null);

  async function createUser(event) {
    event.preventDefault();

    if (!username.trim() || !email.trim()) {
      error = "Both username and email are required.";
      return;
    }

    // Basic email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email.trim())) {
      error = "Enter a valid email address.";
      return;
    }

    loading = true;
    error = "";

    try {
      const response = await fetch(apiUrl(ENDPOINTS.USERS), {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username: username.trim(),
          email: email.trim(),
        }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.log("Error response:", errorText);
        throw new Error(
          `Failed to create user: ${response.status} - ${errorText}`
        );
      }

      const userData = await response.json();
      createdUser = userData;

      // Clear the form
      username = "";
      email = "";
    } catch (err) {
      console.error("Error creating user:", err);
      error = "Failed to create user. Please try again.";
    } finally {
      loading = false;
    }
  }

  function resetForm() {
    createdUser = null;
    username = "";
    email = "";
    error = "";
  }
</script>

<div class="user-creation-container">
  <h2>Create new user</h2>

  {#if !createdUser}
    <form onsubmit={createUser} class="user-form">
      <div class="form-group">
        <label for="username">Username:</label>
        <input
          id="username"
          type="text"
          bind:value={username}
          placeholder="Enter username"
          disabled={loading}
          required
        />
      </div>

      <div class="form-group">
        <label for="email">Email:</label>
        <input
          id="email"
          type="email"
          bind:value={email}
          placeholder="Enter email address"
          disabled={loading}
          required
        />
      </div>

      {#if error}
        <div class="error-message">{error}</div>
      {/if}

      <button
        type="submit"
        class="create-btn"
        disabled={loading || !username.trim() || !email.trim()}
      >
        {loading ? "Creating..." : "Create user"}
      </button>
    </form>
  {:else}
    <div class="success-section">
      <div class="success-message">
        <h3>✅ User Created Successfully!</h3>
        <div class="user-details">
          <p><strong>User ID:</strong> {createdUser.id}</p>
          <p><strong>Username:</strong> {createdUser.username}</p>
          <p><strong>Email:</strong> {createdUser.email}</p>
        </div>
        <div class="user-id-highlight">
          <p>
            <strong>User ID:</strong>
            <span class="user-id">{createdUser.id}</span>
          </p>
        </div>
      </div>

      <div class="action-buttons">
        <button onclick={resetForm} class="create-another-btn">
          Create new user
        </button>
      </div>
    </div>
  {/if}
</div>

<style>
  .user-creation-container {
    max-width: 500px;
    margin: 0 auto;
    padding: 20px;
    font-family: Arial, sans-serif;
  }

  .user-form {
    background-color: #f9f9f9;
    padding: 25px;
    border-radius: 8px;
    border: 1px solid #ddd;
  }

  .form-group {
    margin-bottom: 20px;
  }

  .form-group label {
    display: block;
    margin-bottom: 6px;
    font-weight: bold;
    color: #333;
  }

  .form-group input {
    width: 100%;
    padding: 10px;
    border: 2px solid #ddd;
    border-radius: 6px;
    font-size: 16px;
    transition: border-color 0.2s ease;
  }

  .form-group input:focus {
    outline: none;
    border-color: #007bff;
    box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
  }

  .form-group input:disabled {
    background-color: #e9ecef;
    cursor: not-allowed;
  }

  .create-btn {
    width: 100%;
    padding: 12px;
    background-color: #28a745;
    color: white;
    border: none;
    border-radius: 6px;
    font-size: 16px;
    font-weight: bold;
    cursor: pointer;
    transition: background-color 0.2s ease;
  }

  .create-btn:hover:not(:disabled) {
    background-color: #218838;
  }

  .create-btn:disabled {
    background-color: #6c757d;
    cursor: not-allowed;
  }

  .error-message {
    background-color: #f8d7da;
    color: #721c24;
    padding: 12px;
    border-radius: 6px;
    margin-bottom: 15px;
    border: 1px solid #f5c6cb;
    font-size: 14px;
  }

  .success-section {
    text-align: center;
  }

  .success-message {
    background-color: #d4edda;
    color: #155724;
    padding: 25px;
    border-radius: 8px;
    margin-bottom: 20px;
    border: 1px solid #c3e6cb;
  }

  .user-details {
    margin: 20px 0;
    text-align: left;
  }

  .user-details p {
    margin: 8px 0;
    font-size: 16px;
  }

  .user-id-highlight {
    background-color: #fff3cd;
    border: 1px solid #ffeaa7;
    border-radius: 6px;
    padding: 15px;
    margin-top: 20px;
  }

  .user-id {
    background-color: #fff;
    padding: 4px 8px;
    border-radius: 4px;
    font-family: monospace;
    font-size: 18px;
    font-weight: bold;
    color: #007bff;
    border: 1px solid #ddd;
  }

  .action-buttons {
    display: flex;
    gap: 15px;
    justify-content: center;
    flex-wrap: wrap;
  }

  .create-another-btn {
    padding: 10px 20px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 6px;
    font-size: 14px;
    cursor: pointer;
    transition: background-color 0.2s ease;
  }

  .create-another-btn:hover {
    background-color: #0056b3;
  }

  h2 {
    text-align: center;
    color: #333;
    margin-bottom: 25px;
  }

  h3 {
    margin-top: 0;
  }

  @media (max-width: 480px) {
    .user-creation-container {
      padding: 15px;
    }

    .action-buttons {
      flex-direction: column;
    }

    .action-buttons button {
      width: 100%;
    }
  }
</style>
