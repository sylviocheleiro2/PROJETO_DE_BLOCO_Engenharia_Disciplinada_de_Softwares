const BASE_URL = '/api/pessoas';

async function handleFetchResponse(response) {
    if (!response.ok) {
        // Clona a resposta para poder ler o corpo mais de uma vez, se necessário.
        const clonedResponse = response.clone();
        try {
            // Tenta parsear a resposta como JSON (o formato esperado para erros).
            const errorData = await response.json();
            const message = errorData.message || errorData.title || 'Erro desconhecido.';
            throw new Error(message);
        } catch (e) {
            // Se falhar, lê como texto (plano B para erros inesperados do servidor).
            const textError = await clonedResponse.text();
            throw new Error(textError || `Erro ${response.status}`);
        }
    }
    // Para respostas de sucesso (2xx)
    return response.status === 204 ? null : response.json();
}


export function getPessoas() {
    return fetch(BASE_URL).then(handleFetchResponse);
}

export function getPessoaById(id) {
    return fetch(`${BASE_URL}/${id}`).then(handleFetchResponse);
}

export function createPessoa(person) {
    return fetch(BASE_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(person),
    }).then(handleFetchResponse);
}

export function updatePessoa(id, person) {
    return fetch(`${BASE_URL}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(person),
    }).then(handleFetchResponse);
}

export function deletePessoa(id) {
    return fetch(`${BASE_URL}/${id}`, {
        method: 'DELETE',
    }).then(handleFetchResponse);
}
