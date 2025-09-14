<script>
  import CreateUser from "./lib/CreateUserComponent.svelte";
  import CreatePoll from "./lib/CreatePollComponent.svelte";
  import VotePoll from "./lib/VoteComponent.svelte";
</script>

<main>
  <h1>Pole</h1>

  <div class="card">
    <CreateUser />
    <CreatePoll />
    <VotePoll />
  </div>

</main>

<style>

</style>
