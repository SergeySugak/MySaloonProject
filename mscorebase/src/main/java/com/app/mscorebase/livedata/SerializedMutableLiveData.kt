package com.app.mscorebase.livedata

import java.util.concurrent.LinkedBlockingQueue

/**
 * Класс позволяет порождать очередь объектов одинакового типа,
 * которые будут последовательно обработаны подписчиком SerializedMutableLiveData.
 * Это бывает необходимо, когда порождается сразу много MessageBox-в,
 * которые должны быть отображены все сразу.
 * В стандартном варианте некоторые сообщения могут просто быть утеряны, т.к.
 * очередное сообщение перепишет предыдущее.
 * Что бы иметь возможность вызвать повторную передачу объектов подписчику (например, после смены ориентации устройства)
 * очередь не очищается.
 * Значение null игнорируется в связи с недопустимостью null в LinkedBlockingQueue!
 * Вместо null можно использовать "черную метку".
 */
class SerializedMutableLiveData<T> : StatefulMutableLiveData<T>() {
    private val transQueue = LinkedBlockingQueue<T>(1)
    private val queue = LinkedBlockingQueue<T>()

    private fun superPost(value: T) {
        super.postValue(value)
    }

    override fun postValue(value: T?) {
        //вставляем очередной объект в конец очереди
        //игнорируем null-ы в очереди
        if (value != null) {
            queue.offer(value)
        }
    }

    override fun setValue(value: T?) {
        if (!transQueue.isEmpty() && transQueue.peek() === value) {
            super.setValue(value)
            getValue()
            transQueue.clear()
        } else {
            //вставляем очередной объект в конец очереди
            //игнорируем null-ы в очереди
            if (value != null) {
                queue.offer(value)
            }
        }
    }

    init {
        //организуем поток, который будет блокироваться, когда в очереди нету объектов
        val getThread = Thread(Runnable {
            //TODO говнокод - надо переделывать
            //в бесконечном цикле
            while (true) {
                //если есть подписанные обзерверы
                //(если их нет, то пока прибор переворачивается очередь будет накапливатьь объекты,
                //а после переворота подписчики получат всю накопленную очередь)
                if (hasActiveObservers()) {
                    try {
                        //получаем, и убираем из очереди очередной объект
                        //если в очереди нет объектов, то поток будет заблокирован до тех пор,
                        //пока в ней не появятся объекты
                        val value = queue.take()
                        //Внутри super.postValue(value); вызывает setValue
                        //чего нельзя допустить, потому что текущий объект будет вновь
                        //вставлен в очередь обработки. Чтобы этого избежать
                        //пытаемся записать новый обрабатываемый объект в синхронизированную очередь
                        //ели текущий объект еще не обработан, то поток будет заблокирован домомента
                        //освобождения синхронизированной очереди.
                        //Метод setValue очистит синхронизированную очередь, если записывается объект,
                        //равный находящемуся в ней. Если записывается не равный объект, то он просто
                        //добавится в очередь обработки.
                        transQueue.put(value)
                        //вызываем родительский метод postValue (т.к. мы точно не в MainThread)
                        superPost(value)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        })
        getThread.start()
    }
}