const header = document.querySelector('[data-header]');
const menuButton = document.querySelector('[data-menu-button]');
const nav = document.querySelector('[data-nav]');
const launchSequence = document.querySelector('[data-launch]');
const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

launchSequence?.addEventListener('animationend', (event) => {
  if (event.target === launchSequence && event.animationName === 'launch-exit') {
    launchSequence.remove();
  }
});

const updateHeader = () => header?.classList.toggle('scrolled', window.scrollY > 24);
updateHeader();
window.addEventListener('scroll', updateHeader, { passive: true });

menuButton?.addEventListener('click', () => {
  const open = nav?.classList.toggle('open');
  menuButton.setAttribute('aria-expanded', String(Boolean(open)));
});

nav?.querySelectorAll('a').forEach((link) => link.addEventListener('click', () => {
  nav.classList.remove('open');
  menuButton?.setAttribute('aria-expanded', 'false');
}));

document.querySelectorAll('[data-year]').forEach((node) => {
  node.textContent = String(new Date().getFullYear());
});

const revealNodes = document.querySelectorAll('.reveal');
if (reducedMotion || !('IntersectionObserver' in window)) {
  revealNodes.forEach((node) => node.classList.add('visible'));
} else {
  const revealObserver = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
        revealObserver.unobserve(entry.target);
      }
    });
  }, { threshold: 0.13, rootMargin: '0px 0px -4% 0px' });
  revealNodes.forEach((node) => revealObserver.observe(node));
}

const protocol = document.querySelector('[data-protocol]');
const protocolSteps = [...document.querySelectorAll('[data-protocol-step]')];

const updateProtocol = () => {
  if (!protocol || !protocolSteps.length) return;
  const rect = protocol.getBoundingClientRect();
  const viewportMarker = window.innerHeight * 0.56;
  const progress = Math.max(0, Math.min(1, (viewportMarker - rect.top) / Math.max(1, rect.height)));
  protocol.style.setProperty('--rail-progress', `${progress * 100}%`);

  let activeStep = protocolSteps[0];
  protocolSteps.forEach((step) => {
    if (step.getBoundingClientRect().top < viewportMarker) activeStep = step;
  });
  protocolSteps.forEach((step) => step.classList.toggle('active', step === activeStep));
};

const parallaxRoot = document.querySelector('[data-parallax-root]');
const parallaxTarget = document.querySelector('[data-parallax]');
let pointerX = 0;
let pointerY = 0;
let frameRequested = false;

const renderParallax = () => {
  frameRequested = false;
  if (!parallaxTarget || reducedMotion) return;
  parallaxTarget.style.setProperty('--core-y', `${pointerX * 3.6}deg`);
  parallaxTarget.style.setProperty('--core-x', `${pointerY * -3.1}deg`);
};

parallaxRoot?.addEventListener('pointermove', (event) => {
  const rect = parallaxRoot.getBoundingClientRect();
  pointerX = ((event.clientX - rect.left) / rect.width) - 0.5;
  pointerY = ((event.clientY - rect.top) / rect.height) - 0.5;
  if (!frameRequested) {
    frameRequested = true;
    requestAnimationFrame(renderParallax);
  }
});

parallaxRoot?.addEventListener('pointerleave', () => {
  pointerX = 0;
  pointerY = 0;
  requestAnimationFrame(renderParallax);
});

if (!reducedMotion) {
  updateProtocol();
  window.addEventListener('scroll', updateProtocol, { passive: true });
  window.addEventListener('resize', updateProtocol, { passive: true });
}
