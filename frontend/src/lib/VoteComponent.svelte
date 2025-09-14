<script>
  import { apiUrl, ENDPOINTS } from "./config.js";
  // console.log("Component rendered");
  let pollId = $state("");
  let voterId = $state("");
  let poll = $state(null);
  let selectedOption = $state(null);
  let loading = $state(false);
  let error = $state("");
  let hasVoted = $state(false);

  async function loadPoll() {
    if (!pollId.trim()) {
      error = "Enter a poll ID";
      return;
    }

    loading = true;
    error = "";

    try {
      const response = await fetch(apiUrl(ENDPOINTS.POLLS + `/${pollId}`));

      if (!response.ok) {
        throw new Error(`Poll not found: ${response.status}`);
      }

      poll = await response.json();
      selectedOption = null;
      hasVoted = false;
    } catch (err) {
      console.error("Error loading poll:", err);
      error = "Failed to load poll. Please check the poll ID.";
      poll = null;
    } finally {
      loading = false;
    }
  }

  async function submitVote() {
    if (!voterId.trim()) {
      error = "Enter your voter ID";
      return;
    }

    if (!selectedOption) {
      error = "Select an option";
      return;
    }

    loading = true;
    error = "";

    try {
      const response = await fetch(apiUrl(ENDPOINTS.VOTES), {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          voterId: voterId,
          optionId: selectedOption.id,
          pollId: poll.id,
        }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(
          `Failed to submit vote: ${response.status} - ${errorText}`
        );
      }

      hasVoted = true;
      alert("Vote submitted successfully!");
    } catch (err) {
      console.error("Error submitting vote:", err);
      error = "Failed to submit vote. Please try again.";
    } finally {
      loading = false;
    }
  }

  function selectOption(option) {
    selectedOption = option;
  }
</script>

<div class="voting-container">
  <h2>Vote on a poll</h2>

  <!-- Poll loading -->
  <div class="poll-loader">
    <div class="input-group">
      <label for="pollId">Poll ID:</label>
      <input
        id="pollId"
        type="text"
        bind:value={pollId}
        placeholder="Enter poll ID"
        disabled={loading}
      />
    </div>
    <button onclick={loadPoll} disabled={loading}>
      {loading ? "Loading..." : "Load poll"}
    </button>
  </div>

  <!-- Error display -->
  {#if error}
    <div class="error-message">{error}</div>
  {/if}

  <!-- Poll display and voting -->
  {#if poll && !hasVoted}
    {#key poll.id}
      <div class="poll-section">
        <h3>{poll.question}</h3>
        <p class="poll-meta">
          Published: {new Date(poll.publishedAt).toLocaleString()}
          {#if poll.deadline}
            | Deadline: {new Date(poll.deadline).toLocaleString()}
          {/if}
        </p>

        <div class="voter-input">
          <label for="voterId">Your voter ID:</label>
          <input
            id="voterId"
            type="text"
            bind:value={voterId}
            placeholder="Enter your voter ID"
            disabled={loading}
          />
        </div>

        <div class="options-container">
          <h4>Select an option:</h4>
          {#each poll.options as option (option.id)}
            <div
              class="option-item"
              class:selected={selectedOption?.id === option.id}
            >
              <input
                type="radio"
                id="option-{poll.id}-{option.id}"
                name="poll-option-{poll.id}"
                value={option.id}
                checked={selectedOption?.id === option.id}
                onchange={() => selectOption(option)}
              />
              <label for="option-{poll.id}-{option.id}">
                {option.caption}
              </label>
            </div>
          {/each}
        </div>

        <button
          class="submit-vote-btn"
          onclick={submitVote}
          disabled={loading || !selectedOption || !voterId.trim()}
        >
          {loading ? "Submitting..." : "Submit vote"}
        </button>
      </div>
    {/key}
  {/if}

  <!-- Debug Information -->
  <!-- {#if poll}
    <pre>{JSON.stringify(poll, null, 2)}</pre>
  {/if} -->

  <!-- Success message -->
  {#if hasVoted}
    <div class="success-message">
      <h3>✅ Vote submitted!</h3>
      <p>{poll.question}</p>
      <p>Your selected option: "{selectedOption.caption}"</p>
      <button
        onclick={() => {
          hasVoted = false;
          poll = null;
          pollId = "";
          voterId = "";
        }}
      >
        Vote on another poll
      </button>
    </div>
  {/if}
</div>

<style>
  .voting-container {
    max-width: 600px;
    margin: 0 auto;
    padding: 20px;
    font-family: Arial, sans-serif;
  }

  .poll-loader {
    margin-bottom: 20px;
    padding: 20px;
    border: 1px solid #ddd;
    border-radius: 8px;
    background-color: #f9f9f9;
  }

  .input-group {
    margin-bottom: 15px;
  }

  .input-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
  }

  .input-group input {
    width: 100%;
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-size: 16px;
  }

  .poll-section {
    margin-top: 20px;
    padding: 20px;
    border: 1px solid #ddd;
    border-radius: 8px;
  }

  .poll-meta {
    color: #666;
    font-size: 14px;
    margin-bottom: 20px;
  }

  .voter-input {
    margin-bottom: 20px;
  }

  .voter-input label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
  }

  .voter-input input {
    width: 100%;
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-size: 16px;
  }

  .options-container h4 {
    margin-bottom: 15px;
  }

  .option-item {
    display: flex;
    align-items: center;
    padding: 12px;
    margin-bottom: 8px;
    border: 2px solid #eee;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .option-item:hover {
    border-color: #007bff;
    background-color: #f8f9ff;
  }

  .option-item.selected {
    border-color: #007bff;
    background-color: #e7f3ff;
  }

  .option-item input[type="radio"] {
    margin-right: 10px;
  }

  .option-item label {
    cursor: pointer;
    flex: 1;
    font-size: 16px;
  }

  .submit-vote-btn {
    width: 100%;
    padding: 12px;
    background-color: #28a745;
    color: white;
    border: none;
    border-radius: 6px;
    font-size: 16px;
    cursor: pointer;
    margin-top: 20px;
  }

  .submit-vote-btn:hover:not(:disabled) {
    background-color: #218838;
  }

  .submit-vote-btn:disabled {
    background-color: #6c757d;
    cursor: not-allowed;
  }

  .error-message {
    background-color: #f8d7da;
    color: #721c24;
    padding: 12px;
    border-radius: 6px;
    margin: 10px 0;
    border: 1px solid #f5c6cb;
  }

  .success-message {
    background-color: #d4edda;
    color: #155724;
    padding: 20px;
    border-radius: 6px;
    margin: 20px 0;
    border: 1px solid #c3e6cb;
    text-align: center;
  }

  button {
    padding: 10px 20px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
  }

  button:hover:not(:disabled) {
    background-color: #0056b3;
  }

  button:disabled {
    background-color: #6c757d;
    cursor: not-allowed;
  }
</style>
