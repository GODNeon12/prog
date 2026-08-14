// script.js – UI logic and communication with Java backend

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('promptForm');
    const promptInput = document.getElementById('prompt');
    const outputSection = document.getElementById('outputSection');
    const scriptPre = document.getElementById('scriptResult');
    const errorSection = document.getElementById('errorSection');
    const errorMsg = document.getElementById('errorMessage');
    const copyBtn = document.getElementById('copyBtn');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        // Reset UI
        outputSection.classList.add('hidden');
        errorSection.classList.add('hidden');
        scriptPre.textContent = '';

        const payload = { prompt: promptInput.value.trim() };
        if (!payload.prompt) return;

        try {
            const resp = await fetch('http://localhost:8080/api/generate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const data = await resp.json();
            if (!resp.ok) {
                throw new Error(data.error || 'Unexpected error');
            }

            scriptPre.textContent = data.script;
            outputSection.classList.remove('hidden');
        } catch (err) {
            errorMsg.textContent = err.message;
            errorSection.classList.remove('hidden');
        }
    });

    copyBtn.addEventListener('click', async () => {
        try {
            await navigator.clipboard.writeText(scriptPre.textContent);
            copyBtn.textContent = 'Copied!';
            setTimeout(() => copyBtn.textContent = 'Copy to clipboard', 1500);
        } catch (_) {
            alert('Copy failed – please copy manually.');
        }
    });
});
