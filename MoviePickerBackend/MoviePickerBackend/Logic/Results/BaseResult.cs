namespace MoviePickerBackend.Logic.Results
{
    public abstract class BaseResult
    {
        protected BaseResult(bool success, ErrorResult? error)
        {
            this.Success = success;
            this.Error = error;
        }

        public bool Success { get; }
        public ErrorResult? Error { get; }
    }

    public enum ErrorResult
    {
        NotFound, Closed
    }
}
