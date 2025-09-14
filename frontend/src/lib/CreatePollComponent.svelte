<script>
  import { apiUrl, ENDPOINTS } from "./config.js";
  let userId = $state("");
  let question = $state("");
  let options = $state("");
  let loading = $state(false);
  let error = $state("");
  let createdPoll = $state(null);

  function validateOptions(optionsString) {
    const optionList = optionsString
      .split(",")
      .map((opt) => opt.trim())
      .filter((opt) => opt.length > 0);
    return {
      options: optionList,
      isValid: optionList.length >= 2,
      count: optionList.length,
    };
  }

  // Reactive validation for real-time feedback
  let optionValidation = $derived(validateOptions(options));

  async function createPoll(event) {
    event.preventDefault();

    if (!userId.trim() || !question.trim() || !options.trim()) {
      error = "All fields are required.";
      return;
    }

    const validation = validateOptions(options);

    if (!validation.isValid) {
      error = `Please provide at least 2 options. You currently have ${validation.count}.`;
      return;
    }

    // Check for duplicate options
    const uniqueOptions = [...new Set(validation.options)];
    if (uniqueOptions.length !== validation.options.length) {
      error = "Please remove duplicate options.";
      return;
    }

    loading = true;
    error = "";

    try {
      const response = await fetch(apiUrl(ENDPOINTS.POLLS), {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          userId: parseInt(userId.trim()),
          question: question.trim(),
          options: validation.options,
        }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(
          `Failed to create poll: ${response.status} - ${errorText}`
        );
      }

      const data = await response.json();
      createdPoll = data;

      // Clear form
      userId = "";
      question = "";
      options = "";
    } catch (err) {
      console.error("Error creating poll:", err);
      error = "Error creating poll. Please try again.";
    } finally {
      loading = false;
    }
  }

  function resetForm() {
    createdPoll = null;
    userId = "";
    question = "";
    options = "";
    error = "";
  }
</script>

<div class="create-poll-container">
  <h2>Create new poll</h2>

  {#if !createdPoll}
    <form onsubmit={createPoll} class="poll-form">
      <div class="form-group">
        <label for="userId">User ID:</label>
        <input
          id="userId"
          type="text"
          bind:value={userId}
          placeholder="Enter your user ID"
          disabled={loading}
          required
        />
      </div>

      <div class="form-group">
        <label for="question">Question:</label>
        <input
          id="question"
          type="text"
          bind:value={question}
          placeholder="Enter your poll question"
          disabled={loading}
          required
        />
      </div>

      <div class="form-group">
        <label for="options">Options (comma separated):</label>
        <input
          id="options"
          type="text"
          bind:value={options}
          placeholder="Option 1, Option 2, Option 3..."
          disabled={loading}
          required
        />
        <div class="options-help">
          <small>
            {#if options.trim()}
              {optionValidation.count} option{optionValidation.count !== 1
                ? "s"
                : ""} detected
              {#if optionValidation.count < 2}
                <span class="warning">⚠️ Need at least 2 options</span>
              {:else}
                <span class="success">✅ Valid</span>
              {/if}
            {:else}
              Separate each option with a comma
            {/if}
          </small>
        </div>

        <!-- Preview options -->
        {#if optionValidation.options.length > 0}
          <div class="options-preview">
            <strong>Preview:</strong>
            <ul>
              {#each optionValidation.options as option, index}
                <li>{index + 1}. {option}</li>
              {/each}
            </ul>
          </div>
        {/if}
      </div>

      {#if error}
        <div class="error-message">{error}</div>
      {/if}

      <button
        type="submit"
        class="create-btn"
        disabled={loading ||
          !optionValidation.isValid ||
          !userId.trim() ||
          !question.trim()}
      >
        {loading ? "Creating poll..." : "Create poll"}
      </button>
    </form>
  {:else}
    <div class="success-section">
      <div class="success-message">
        <h3>✅ Poll created successfully!</h3>
        <div class="poll-details">
          <p><strong>Poll ID:</strong> {createdPoll.id}</p>
          <p><strong>Question:</strong> {createdPoll.question}</p>
          <p><strong>Options:</strong></p>
          <ul>
            {#each createdPoll.options as option}
              <li>{option.caption}</li>
            {/each}
          </ul>
        </div>
      </div>

      <div class="action-buttons">
        <button onclick={resetForm} class="create-another-btn">
          Create another poll
        </button>
      </div>
    </div>
  {/if}
</div>

<style>
  .create-poll-container {
    max-width: 600px;
    margin: 0 auto;
    padding: 20px;
    font-family: Arial, sans-serif;
  }

  .poll-form {
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

  .options-help {
    margin-top: 5px;
  }

  .options-help small {
    color: #666;
  }

  .warning {
    color: #dc3545;
    font-weight: bold;
  }

  .success {
    color: #28a745;
    font-weight: bold;
  }

  .options-preview {
    margin-top: 10px;
    padding: 10px;
    background-color: #f8f9fa;
    border-radius: 4px;
    border: 1px solid #dee2e6;
  }

  .options-preview ul {
    margin: 5px 0 0 0;
    padding-left: 20px;
  }

  .options-preview li {
    margin-bottom: 2px;
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

  .poll-details {
    text-align: left;
    margin: 20px 0;
  }

  .poll-details ul {
    margin-top: 5px;
  }

  .action-buttons {
    display: flex;
    gap: 15px;
    justify-content: center;
    flex-wrap: wrap;
  }

  .create-another-btn {
    padding: 10px 20px;
    border: none;
    border-radius: 6px;
    font-size: 14px;
    cursor: pointer;
    transition: background-color 0.2s ease;
  }

  .create-another-btn {
    background-color: #007bff;
    color: white;
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
    .create-poll-container {
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
