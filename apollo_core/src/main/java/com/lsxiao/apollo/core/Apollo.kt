package com.lsxiao.apollo.core

import com.lsxiao.apollo.core.contract.ApolloBinder
import com.lsxiao.apollo.core.contract.ApolloBinderGenerator
import com.lsxiao.apollo.core.entity.ApolloBinderImpl
import com.lsxiao.apollo.core.entity.Event
import com.lsxiao.apollo.core.entity.SchedulerProvider
import com.lsxiao.apollo.core.serialize.KryoSerializer
import com.lsxiao.apollo.core.serialize.Serializable
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor
import kotlin.properties.Delegates


/**
 * author lsxiao
 * date 2016-05-09 17:27
 */
class Apollo private constructor() {
    private val mFlowableProcessor: FlowableProcessor<Event> by lazy {
        PublishProcessor.create<Event>().toSerialized()
    }
    //用于保存stick事件
    private val mStickyEventMap: MutableMap<String, Event> = HashMap()
    //用于保存SubscriptionBinder
    private val mBindTargetMap: MutableMap<Int, ApolloBinder?> = HashMap()
//    private var mApolloBinderGenerator: ApolloBinderGenerator by Delegates.notNull()
    private var mSchedulerProvider: SchedulerProvider by Delegates.notNull()
    private var mContext: Any by Delegates.notNull()
    private var mSerializer: Serializable = KryoSerializer()
    private var mIPCEnable = false
    private var mIsApolloBinderClassNotFound = false
    private var mIsIPCModuleClassNotFound = false
    //添加各个module的ApolloBinderGenerator
    private val mApolloBinderGeneratorList: MutableList<ApolloBinderGenerator> = ArrayList()
    //从这里获取需要generator去generate
    private val mInvokeClassesMap: MutableMap<ApolloBinderGenerator, List<String>> = HashMap()

    companion object {
        private var sInstance: Apollo? = null

        /**
         * 返回一个Apollo的单例对象

         * @return Apollo
         */
        @JvmStatic
        @Synchronized
        private fun get(): Apollo {
            if (null == Apollo.Companion.sInstance) {
                Apollo.Companion.sInstance = Apollo()
            }
            return Apollo.Companion.sInstance as Apollo
        }

        @Deprecated(message = "this method is not support ipc", replaceWith = ReplaceWith("use init(AndroidSchedulers.mainThread(), ApolloBinderGeneratorImpl.instance(), getApplicationContext()) to instead"), level = DeprecationLevel.HIDDEN)
        @JvmStatic
        fun init(main: Scheduler, binder: ApolloBinderGenerator) {
            Apollo.Companion.init(main, binder, Any())
        }

        @Deprecated(message = "this method is not support ipc", replaceWith = ReplaceWith("use init(AndroidSchedulers.mainThread(), getApplicationContext()) to instead"), level = DeprecationLevel.WARNING)
        @JvmStatic
        fun init(main: Scheduler, binder: ApolloBinderGenerator, context: Any) {
            init(main, context)
        }

        @Deprecated(message = "this method is not support ipc", replaceWith = ReplaceWith("use init(AndroidSchedulers.mainThread(), getApplicationContext(),true) to instead"), level = DeprecationLevel.WARNING)
        @JvmStatic
        fun init(main: Scheduler, binder: ApolloBinderGenerator, context: Any, ipcEnable: Boolean = false) {
            init(main, context, ipcEnable)
        }

        @JvmStatic
        fun init(main: Scheduler, context: Any) {
            init(main, context, false)
        }

        @JvmStatic
        fun init(main: Scheduler, context: Any, ipcEnable: Boolean = false) {
            //now we add ApolloBinderGenerator manually
//            try {
//                val generatorImplClass = Class.forName("com.lsxiao.apollo.generate.ApolloBinderGeneratorImpl") as Class<ApolloBinderGenerator>
//                val staticInstanceMethod = generatorImplClass.getMethod("instance")
//                val generator = staticInstanceMethod.invoke(null) as ApolloBinderGenerator
//
//                Apollo.get().mApolloBinderGenerator = generator
//
//            } catch (e: ClassNotFoundException) {
//                Apollo.get().mIsApolloBinderClassNotFound = true
//            }
            Apollo.get().mSchedulerProvider = SchedulerProvider.Companion.create(main)
            Apollo.get().mContext = context
            Apollo.get().mIPCEnable = ipcEnable

            if (ipcEnable) {
                registerIPCReceiver(context)
            }

        }

        /**
         * 生成类后使用，这样就能传递信息了
         */
        @JvmStatic
        fun addApolloBinderGeneratorImpl(className: String) {
            //now we add ApolloBinderGenerator manually
            val fullClassName = "com.lsxiao.apollo.generate." + className
            try {
                val generatorImplClass = Class.forName(fullClassName) as Class<ApolloBinderGenerator>
                val staticInstanceMethod = generatorImplClass.getMethod("instance")
                val generator = staticInstanceMethod.invoke(null) as ApolloBinderGenerator
                //加入列表
                Apollo.get().mApolloBinderGeneratorList.add(generator)
                //将字符串连接起来，放入map，然后用于绑定
                if (!generator.getInvokeClass().isNullOrBlank()) {
                    var strList : MutableList<String> = ArrayList()
                    strList.addAll(generator.getInvokeClass().split(","))
                    strList.remove("")
                    Apollo.get().mInvokeClassesMap.put(generator, strList)
                }
            } catch (e: ClassNotFoundException) {
                Apollo.get().mIsApolloBinderClassNotFound = true
            }
        }

        private fun registerIPCReceiver(context: Any) {
            try {
                val intentFilterClass = Class.forName("android.content.IntentFilter")

                val intentFilterConstructor = intentFilterClass.getConstructor(String::class.java)

                val broadcastReceiverClass = Class.forName("android.content.BroadcastReceiver")

                val ipcBroadcastReceiverClass = Class.forName("com.lsxiao.apollo.ipc.ApolloProcessEventReceiver")

                val registerBroadcastReceiverMethod = context.javaClass.getMethod("registerReceiver", broadcastReceiverClass, intentFilterClass)

                registerBroadcastReceiverMethod.invoke(context, ipcBroadcastReceiverClass.newInstance(), intentFilterConstructor.newInstance("apollo"))
            } catch (ignore: ClassNotFoundException) {
                Apollo.get().mIsIPCModuleClassNotFound = true
            }
        }


        @JvmStatic
        fun serializer(serializer: Serializable) {
            Apollo.get().mSerializer = serializer
        }

        @JvmStatic
        fun getSerializer() = Apollo.get().mSerializer


        /**
         * 判断是否有订阅者
         */
        @JvmStatic
        fun hasSubscribers(): Boolean {
            return Apollo.get().mFlowableProcessor.hasSubscribers()
        }

        /**
         * 绑定Activity或者Fragment

         * @param o Object
         * *
         * @return ApolloBinder
         */
        @JvmStatic
        fun bind(o: Any?): ApolloBinder? {
            if (null == o) {
                throw java.lang.NullPointerException("object to subscribe must not be null")
            }

            return Apollo.Companion.uniqueBind(o)
        }


        /**
         * 绑定Activity或者Fragment

         * @param o Object
         * *
         * @return ApolloBinder
         */
        @JvmStatic
        fun isBind(o: Any?): Boolean {
            if (o == null) {
                return false
            }
            val uniqueId = System.identityHashCode(o)
            return Apollo.get().mBindTargetMap.containsKey(uniqueId)
        }

        /**
         * 唯一绑定,避免重复绑定到相同的对象

         * @param obj Object
         * *
         * @return ApolloBinder
         */
        @JvmStatic
        private fun uniqueBind(obj: Any): ApolloBinder? {
            //找不到生成的绑定类
            if (Apollo.get().mIsApolloBinderClassNotFound) {
                return ApolloBinderImpl(obj)
            }

            val uniqueId = System.identityHashCode(obj)

            var binder : ApolloBinder? = null

            var binderGenerator : ApolloBinderGenerator?

            //对象已有绑定记录
            if (Apollo.get().mBindTargetMap.containsKey(uniqueId)) {
                binder = Apollo.get().mBindTargetMap[uniqueId] as ApolloBinder
                //绑定已经解绑
                if (binder.isUnbind()) {
                    //移除已经解绑的binder
                    Apollo.get().mBindTargetMap.remove(uniqueId)
                    //重新绑定
                    binderGenerator = getInvokeGenerator(obj)
                    binderGenerator?.let {
                        binder = it.generate(obj)
                        //保存到map中
                        Apollo.get().mBindTargetMap.put(uniqueId, binder)
                    }
//                    binder = Apollo.get().mApolloBinderGenerator.generate(obj)
                }
            } else {
                binderGenerator = getInvokeGenerator(obj)
                binderGenerator?.let {
                    binder = it.generate(obj)
                    //保存到map中
                    Apollo.get().mBindTargetMap.put(uniqueId, binder)
                }
//                binder = Apollo.get().mApolloBinderGenerator.generate(obj)
            }
            return binder
        }


        @JvmStatic
        private fun getInvokeGenerator(obj: Any) : ApolloBinderGenerator? {
            var targetGenerator  : ApolloBinderGenerator? =null
            Apollo.get().mApolloBinderGeneratorList.forEach {generator ->
                Apollo.get().mInvokeClassesMap[generator]?.forEach { it
                    if (it == obj.javaClass.name) {
                        targetGenerator =  generator
                    }
                }
            }
            return targetGenerator
        }

        @JvmStatic
        internal fun unBind(o: Any) {
            val uniqueId = System.identityHashCode(o)
            Apollo.get().mBindTargetMap.remove(uniqueId)
        }

        @JvmStatic
        fun toFlowable(tag: String): Flowable<Any> {
            return Apollo.Companion.toFlowable(arrayOf(tag), Any::class.java)
        }

        @JvmStatic
        fun toFlowable(tags: Array<String>): Flowable<Any> {
            return Apollo.Companion.toFlowable(tags, Any::class.java)
        }

        @JvmStatic
        fun <T> toFlowable(tags: Array<String>?, eventType: Class<T>?): Flowable<T> {
            if (null == eventType) {
                throw java.lang.NullPointerException("the eventType must be not null")
            }

            if (null == tags) {
                throw java.lang.NullPointerException("the tags must be not null")
            }

            if (tags.isEmpty()) {
                throw java.lang.IllegalArgumentException("the tags must be not empty")
            }

            return Apollo.get().mFlowableProcessor
                    .filter { event ->
                        java.util.Arrays.asList(*tags).contains(event.tag) && eventType.isInstance(event.data)
                    }
                    .flatMap { event -> Flowable.just(eventType.cast(event.data)) }
        }

        @JvmStatic
        fun toFlowableSticky(tag: String): Flowable<Any> {
            return Apollo.Companion.toFlowableSticky(arrayOf(tag))
        }

        @JvmStatic
        fun toFlowableSticky(tags: Array<String>): Flowable<Any> {
            return Apollo.Companion.toFlowableSticky(tags, Any::class.java)
        }

        @JvmStatic
        fun <T> toFlowableSticky(tags: Array<String>?, eventType: Class<T>?): Flowable<T> {
            if (null == eventType) {
                throw java.lang.NullPointerException("the eventType must be not null")
            }

            if (null == tags) {
                throw java.lang.NullPointerException("the tags must be not null")
            }

            if (tags.isEmpty()) {
                throw java.lang.IllegalArgumentException("the tags must be not empty")
            }

            synchronized(Apollo.get().mStickyEventMap) {
                //普通事件的被观察者
                val flowable = Apollo.Companion.toFlowable(tags, eventType)

                val stickyEvents = java.util.ArrayList<Event>()
                for (tag in tags) {
                    //sticky事件
                    val event = Apollo.get().mStickyEventMap[tag]
                    if (event != null) {
                        Apollo.get().mStickyEventMap[tag]?.let { stickyEvents.add(it) }
                    }
                }

                if (!stickyEvents.isEmpty()) {
                    //合并事件序列
                    return Flowable.fromIterable(stickyEvents)
                            .flatMap { event -> Flowable.just(eventType.cast(event.data)) }.mergeWith(flowable)

                } else {
                    return flowable
                }
            }
        }

        @JvmStatic
        fun getSchedulerProvider(): SchedulerProvider = Apollo.get().mSchedulerProvider

        @JvmStatic
        fun getContext(): Any = Apollo.get().mContext


        /**
         * ipc转发event
         */
        @JvmStatic
        fun transfer(event: Event) = synchronized(Apollo.get().mStickyEventMap) {
            if (Apollo.get().mIPCEnable) {
                if (event.isSticky) {
                    Apollo.get().mStickyEventMap.put(event.tag, event)
                }
                Apollo.get().mFlowableProcessor.onNext(event)
            }
        }

        @JvmStatic
        fun emit(tag: String) = synchronized(Apollo.get().mStickyEventMap) {
            Apollo.Companion.emit(tag, Any(), false)
        }

        @JvmStatic
        fun emit(tag: String, actual: Any = Any()) = synchronized(Apollo.get().mStickyEventMap) {
            Apollo.Companion.emit(tag, actual, false)
        }

        @JvmStatic
        fun emit(tag: String, sticky: Boolean = false) = synchronized(Apollo.get().mStickyEventMap) {
            Apollo.Companion.emit(tag, Any(), sticky)
        }

        @JvmStatic
        fun emit(tag: String, actual: Any = Any(), sticky: Boolean = false) = synchronized(Apollo.get().mStickyEventMap) {
            val event = Event(tag, actual, ProcessUtil.getPid(), sticky)
            if (sticky) {
                Apollo.get().mStickyEventMap.put(tag, event)
            }
            Apollo.get().mFlowableProcessor.onNext(event)

            //推送消息到其他进程
            if (Apollo.get().mIPCEnable) {

                if (Apollo.get().mIsApolloBinderClassNotFound) {
                    throw Exception("the ApolloBinderGeneratorImpl class is not found which is generated at compile time")
                }

                if (Apollo.get().mIsIPCModuleClassNotFound) {
                    throw Exception("the ApolloProcessEventReceiver class is not found which belong to ipc module,you must depend on com.github.lsxiao.Apollo:ipc:latest")
                }
                if (Apollo.get().mApolloBinderGeneratorList.isNotEmpty()) {
                    Apollo.get().mApolloBinderGeneratorList[0].broadcastEvent(event)
                }
//                Apollo.get().mApolloBinderGenerator.broadcastEvent(event)
            }

        }

        @JvmStatic
        fun removeStickyEvent(vararg tags: String) = tags.forEach { tag ->
            synchronized(Apollo.get().mStickyEventMap) {
                Apollo.get().mStickyEventMap.remove(tag)
            }
        }

        @JvmStatic
        fun removeAllStickyEvent() = Apollo.get().mStickyEventMap.clear()


        /**
         * 根据tag和eventType获取指定类型的Sticky事件
         */
        @JvmStatic
        fun <T> getStickyEvent(tag: String, eventType: Class<T>): T? {
            synchronized(Apollo.get().mStickyEventMap) {
                val o = Apollo.get().mStickyEventMap[tag]?.data as Any
                if (o.javaClass.canonicalName == eventType.canonicalName) {
                    return eventType.cast(o)
                }
            }
            return null
        }

        /**
         * 根据tag获取Sticky事件
         */
        @JvmStatic
        fun getStickyEvent(tag: String): Any? {
            synchronized(Apollo.get().mStickyEventMap) {
                return if (Apollo.get().mStickyEventMap[tag] == null) null else Apollo.get().mStickyEventMap[tag]?.data
            }
        }
    }

}