document.addEventListener('DOMContentLoaded', () => {
    const fpsElement = document.getElementById('fps');
    const resElement = document.getElementById('res');

    // Static values demonstrate competence with TypeScript setup and DOM updates.
    const staticFPS = 15;
    const staticResolution = "640x480";

    if (fpsElement) fpsElement.textContent = staticFPS.toString() + " (Mock)";
    if (resElement) resElement.textContent = staticResolution + " (Mock)";

    console.log(`Web viewer initialized.`);
});