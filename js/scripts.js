const API_URL = "http://localhost:8080/veiculo";
let pagina = 0;

document.getElementById("veiculoForm").addEventListener("submit", async function (e) {
  e.preventDefault(); 

  const id = document.getElementById("veiculoId").value;
  const veiculo = document.getElementById("veiculo").value;
  const marca = document.getElementById("marca").value;
  const ano = parseInt(document.getElementById("ano").value);

  let dados = { id: parseInt(id), veiculo, marca, ano };

  let metodo = "POST"; 
  let url = API_URL;

  if (id) {
    metodo = "PUT"; 
    url = `${API_URL}/${id}`;
    dados = { id: parseInt(id), veiculo, marca, ano }; 
  }

  try {
    const resposta = await fetch(url, {
      method: metodo,
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(dados),
    });

    if (!resposta.ok) {
      throw new Error("Erro ao salvar o veículo");
    }

    resetarFormulario();
    carregarVeiculos(); 
  } catch (erro) {
    alert(`Erro: ${erro.message}`);
  }
});

function resetarFormulario() {
  document.getElementById("veiculoId").value = "";
  document.getElementById("veiculo").value = "";
  document.getElementById("marca").value = "";
  document.getElementById("ano").value = "";
}

async function carregarVeiculos() {
  try {
    const resposta = await fetch(`${API_URL}?page=${pagina}&size=5`);
    if (!resposta.ok) {
      throw new Error("Erro ao carregar os veículos");
    }

    const dados = await resposta.json();
    const veiculos = dados.content;

    const tabela = document.getElementById("tabelaVeiculos");
    tabela.innerHTML = "";

    veiculos.forEach((v) => {
      const linha = document.createElement("tr");
      linha.innerHTML = `
                <td>${v.id}</td>
                <td>${v.veiculo}</td>
                <td>${v.marca}</td>
                <td>${v.ano}</td>
                <td>
                    <button onclick="editarVeiculo(${v.id})">Editar</button>
                    <button onclick="deletarVeiculo(${v.id})">Excluir</button>
                </td>
            `;
      tabela.appendChild(linha);
    });

    document.getElementById("paginaAtual").innerText = pagina + 1;
  } catch (erro) {
    alert(`Erro ao carregar veículos: ${erro.message}`);
  }
}

async function editarVeiculo(id) {
  try {
    const resposta = await fetch(`${API_URL}/${id}`);
    if (!resposta.ok) {
      throw new Error("Erro ao carregar veículo");
    }

    const veiculo = await resposta.json();
    document.getElementById("veiculoId").value = veiculo.id;
    document.getElementById("veiculo").value = veiculo.veiculo;
    document.getElementById("marca").value = veiculo.marca;
    document.getElementById("ano").value = veiculo.ano;
  } catch (erro) {
    alert(`Erro ao editar veículo: ${erro.message}`);
  }
}

async function deletarVeiculo(id) {
  if (confirm("Tem certeza que deseja excluir este veículo?")) {
    try {
      const resposta = await fetch(`${API_URL}/${id}`, { method: "DELETE" });
      if (!resposta.ok) {
        throw new Error("Erro ao excluir veículo");
      }

      carregarVeiculos(); 
    } catch (erro) {
      alert(`Erro ao excluir veículo: ${erro.message}`);
    }
  }
}

document.getElementById("anterior").addEventListener("click", () => {
  if (pagina > 0) {
    pagina--;
    carregarVeiculos();
  }
});

document.getElementById("proximo").addEventListener("click", () => {
  pagina++;
  carregarVeiculos();
});

carregarVeiculos(); 
