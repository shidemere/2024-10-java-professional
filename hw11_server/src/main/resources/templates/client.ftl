<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Управление клиентами</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        body {
            display: flex;
            flex-direction: column;
            align-items: center;
            min-height: 100vh;
            background: linear-gradient(45deg, #ff6b6b, #4ecdc4);
            padding: 40px 20px;
            overflow: hidden;
        }

        .container {
            width: 100%;
            max-width: 800px;
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
            backdrop-filter: blur(10px);
            transform: scale(0.95);
            transition: all 0.5s;
        }

        .container:hover {
            transform: scale(1);
        }

        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 30px;
            font-weight: 600;
            letter-spacing: 1px;
        }

        .clients-list {
            margin-bottom: 30px;
        }

        .client-card {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 15px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: transform 0.3s;
        }

        .client-card:hover {
            transform: translateY(-3px);
        }

        .client-name {
            font-size: 20px;
            color: #333;
            margin-bottom: 10px;
            font-weight: 500;
        }

        .client-details {
            color: #666;
            line-height: 1.6;
        }

        .add-form {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }

        input, button {
            padding: 12px;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            transition: all 0.3s;
        }

        input {
            background: #f0f0f0;
        }

        input:focus {
            outline: none;
            background: #fff;
            box-shadow: 0 0 10px rgba(78, 205, 196, 0.2);
        }

        button {
            background: #4ecdc4;
            color: white;
            cursor: pointer;
            font-weight: 600;
        }

        button:hover {
            background: #45b7b0;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(78, 205, 196, 0.4);
        }

        .phone-fields {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .add-phone {
            align-self: flex-start;
            padding: 8px 15px;
            font-size: 14px;
        }

        /* Пузырьки на фоне */
        .bubbles {
            position: fixed;
            width: 100%;
            height: 100%;
            z-index: -1;
            pointer-events: none;
        }

        .bubbles span {
            position: absolute;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 50%;
            animation: float 10s linear infinite;
        }

        @keyframes float {
            0% { transform: translateY(100vh) scale(0); }
            100% { transform: translateY(-10vh) scale(1); }
        }

        @media (max-width: 680px) {
            .container {
                width: 90%;
                padding: 20px;
            }

            .client-name {
                font-size: 18px;
            }
        }
    </style>
</head>
<body>
<div class="bubbles">
    <span style="left: 10%; animation-delay: 0s;"></span>
    <span style="left: 20%; animation-delay: 2s;"></span>
    <span style="left: 50%; animation-delay: 5s;"></span>
    <span style="left: 70%; animation-delay: 7s;"></span>
</div>

<div class="container">
    <h1>Управление клиентами</h1>

    <div class="clients-list">
        <#list clients as client>
            <div class="client-card">
                <div class="client-name">${client.name}</div>
                <div class="client-details">
                    <div>Адрес: ${client.address.street!''}</div>
                    <div>Телефоны:
                        <#if client.phones??>
                            <#list client.phones as phone>${phone.number}<#sep>, </#sep></#list>
                        <#else>
                            Нет данных
                        </#if>
                    </div>
                </div>
            </div>
        </#list>
    </div>

    <!-- Форма добавления клиента -->
    <form class="add-form" action="/client" method="POST">
        <input type="text" name="name" placeholder="Имя клиента" required>
        <input type="text" name="street" placeholder="Улица" required>

        <div class="phone-fields" id="phoneFields">
            <input type="text" name="phones" placeholder="Телефон" required>
        </div>

        <button type="button" class="add-phone" onclick="addPhoneField()">+ Добавить телефон</button>
        <button type="submit">Добавить клиента</button>
    </form>
</div>

<script>
    function addPhoneField() {
        const phoneFields = document.getElementById('phoneFields');
        const newField = document.createElement('input');
        newField.type = 'text';
        newField.name = 'phones';
        newField.placeholder = 'Телефон';
        newField.required = true;
        phoneFields.appendChild(newField);
    }
</script>
</body>
</html>