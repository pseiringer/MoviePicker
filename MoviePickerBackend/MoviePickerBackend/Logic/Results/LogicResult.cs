using static System.Runtime.InteropServices.JavaScript.JSType;

namespace MoviePickerBackend.Logic.Results
{

    public class LogicResult<T> : BaseResult
    {
        public LogicResult(T value) : base(true, null)
        {
            this.Value = value;
        }

        public LogicResult(ErrorResult error) : base(false, error)
        {
            this.Value = default;
        }

        public T? Value { get; }

        public override bool Equals(object? obj)
        {
            return obj is LogicResult<T> result &&
                   Success == result.Success &&
                   Error == result.Error &&
                   EqualityComparer<T?>.Default.Equals(Value, result.Value);
        }

        public override int GetHashCode()
        {
            return HashCode.Combine(Success, Error, Value);
        }
    }

    public class LogicResult : LogicResult<object?>
    {
        public LogicResult() : base(null) { }

        public LogicResult(ErrorResult error) : base(error) { }

        public override bool Equals(object? obj)
        {
            return obj is LogicResult result &&
                   base.Equals(obj) &&
                   Success == result.Success &&
                   Error == result.Error &&
                   EqualityComparer<object?>.Default.Equals(Value, result.Value);
        }

        public override int GetHashCode()
        {
            return HashCode.Combine(base.GetHashCode(), Success, Error, Value);
        }
    }
}
